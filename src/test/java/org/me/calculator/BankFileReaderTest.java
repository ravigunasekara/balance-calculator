package org.me.calculator;

import org.junit.jupiter.api.Test;
import org.me.calculator.model.ResponseModel;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankFileReaderTest {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    @Test
    void findRecordsFromFile () {

        LocalDateTime startDateTime = LocalDateTime.parse("20/10/2018 12:00:55", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("20/10/2018 20:00:55", formatter);

        ResponseModel responseModel = null;
        BankFileReader bankFileReader = new BankFileReader();
        try {
            responseModel = bankFileReader.findRawBatch(startDateTime, endDateTime, "transaction.csv", "ACC334455");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(1, responseModel.getNoOfTransactions());
        assertEquals(new BigDecimal("-25.00"), responseModel.getAccountBalance());
    }

}