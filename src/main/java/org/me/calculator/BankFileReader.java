package org.me.calculator;

import com.univocity.parsers.common.processor.BatchedColumnProcessor;
import org.me.calculator.model.ResponseModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.me.calculator.common.DateFormatter.findValidDateTime;

public class BankFileReader {

    private static final Logger log = LoggerFactory.getLogger(BankFileReader.class);

    private static final int TUMBLING_WINDOW_SIZE = 10;

    public ResponseModel findRawBatch(LocalDateTime startDateTime,
                                      LocalDateTime endDateTime,
                                      String filePath,
                                      String accountId) throws UnsupportedEncodingException, FileNotFoundException {
        try {
            Map<String, BigDecimal> selectedTxn = new HashMap<>();
            TransactionProcessor transactionProcessor = new TransactionProcessor(selectedTxn);

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
                        LocalDateTime date = findValidDateTime(rawValues.get(rawValues.size() - 1));
                        if (startDateTime.isBefore(date)) {
                            transactionProcessor.processRecords(columnValues,
                                    startDateTime,
                                    endDateTime,
                                    accountId);
                        }
                    }
                }
            };
            settings.setProcessor(columnRowProcessor);
            CsvParser parser = new CsvParser(settings);
            parser.parse(inputReader);

            return new ResponseModel(selectedTxn.values());
        } catch (Exception e) {
            log.error("Invalid input data : Assumption -Input file and records are all in a valid format ", e);
            throw e;
        }
    }
}
