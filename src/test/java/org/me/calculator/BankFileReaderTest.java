package org.me.calculator;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankFileReaderTest {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    @Test
    void findRecordsFromFile () {

        LocalDateTime startDateTime = LocalDateTime.parse("20/10/2018 12:00:55", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("20/10/2018 20:00:55", formatter);

        Map<String, BigDecimal> selectedTxn = new HashMap<>();
        BankFileReader bankFileReader = new BankFileReader(selectedTxn);
        try {
            selectedTxn = bankFileReader.findRawBatch(startDateTime, endDateTime, "transaction.csv", "ACC334455");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BigDecimal total = selectedTxn.values().stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
        assertEquals(1, selectedTxn.size());
        assertEquals(new BigDecimal("-25.00"), total);
    }

}