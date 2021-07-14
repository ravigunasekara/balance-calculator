package org.me.calculator;

import com.univocity.parsers.common.processor.BatchedColumnProcessor;
import org.me.calculator.model.TxnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class BankFileReader {

    private static final Logger log = LoggerFactory.getLogger(BankFileReader.class);

    private static final int TUMBLING_WINDOW_SIZE = 10;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Map<String, BigDecimal> selectedTxn;

    public BankFileReader(Map<String, BigDecimal> selectedTxn) {
        this.selectedTxn = selectedTxn;
    }

    public Map<String, BigDecimal> findRawBatch(LocalDateTime startDateTime,
                                                LocalDateTime endDateTime,
                                                String filePath,
                                                String accountId) throws UnsupportedEncodingException, FileNotFoundException {
        try {

            Reader inputReader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
            CsvParserSettings settings = new CsvParserSettings();
            settings.setColumnReorderingEnabled(false);
            settings.setHeaderExtractionEnabled(true);

            BatchedColumnProcessor columnRowProcessor = new BatchedColumnProcessor(TUMBLING_WINDOW_SIZE) {
                @Override
                public void batchProcessed(int rowsInThisBatch) {
                    Map<Integer, List<String>> columnValues = getColumnValuesAsMapOfIndexes();
                    List<String> rawValues = columnValues.get(3);

                    if (rawValues.size() > 0) {
                        LocalDateTime date = LocalDateTime.parse(rawValues.get(rawValues.size() - 1), formatter);
                        if (startDateTime.isBefore(date)) {
                            processRecords(columnValues, startDateTime, endDateTime, accountId);
                        }
                    }
                }
            };

            settings.setProcessor(columnRowProcessor);

            CsvParser parser = new CsvParser(settings);
            parser.parse(inputReader);
            return selectedTxn;
        } catch (Exception e) {
            log.error("Invalid input data : Assumption -Input file and records are all in a valid format ", e);
            throw e;
        }
    }

    private void processRecords(Map<Integer, List<String>> columnValues,
                                LocalDateTime startDateTime,
                                LocalDateTime endDateTime,
                                String accountId) {
        List<String> rawValues = columnValues.get(3);

        int pointer = 0;
        for (String date : rawValues) {
            LocalDateTime recordDate = LocalDateTime.parse(date, formatter);
            if (recordDate.compareTo(startDateTime) > -1) {
                List<String> txnType = columnValues.get(5);
                List<String> reversalIds = columnValues.get(6);
                if (recordDate.compareTo(endDateTime) < 0) {
                    processPaymentTransaction(columnValues, txnType, pointer, accountId);
                }
                if (txnType.get(pointer).equals(TxnType.REVERSAL.name())) {
                    selectedTxn.remove(reversalIds.get(pointer));
                }
            }
            pointer++;
        }
    }

    private void processPaymentTransaction(Map<Integer, List<String>> columnValues,
                                           List<String> txnType,
                                           int pointer,
                                           String accountId) {
        List<String> txnIds = columnValues.get(0);
        List<String> fromAccountIds = columnValues.get(1);
        List<String> toAccountIds = columnValues.get(2);
        List<String> amount = columnValues.get(4);
        if (txnType.get(pointer).equals(TxnType.PAYMENT.name())) {
            if (fromAccountIds.get(pointer).equals(accountId)) {
                BigDecimal num = new BigDecimal(amount.get(pointer));
                selectedTxn.put(txnIds.get(pointer), num.negate());
            }
            if (toAccountIds.get(pointer).equals(accountId)) {
                BigDecimal num = new BigDecimal(amount.get(pointer));
                selectedTxn.put(txnIds.get(pointer), num);
            }
        }
    }

}
