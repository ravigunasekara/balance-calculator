package org.me.calculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.calculator.model.ResponseModel;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BankFileReaderIntegrationTest {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private Path resourceDirectory = Paths.get("src","test","resources");

    private ResponseModel responseModel;
    private BankFileReader bankFileReader;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;

    @BeforeEach
    void setup() {
        bankFileReader = new BankFileReader();
        startDateTime = LocalDateTime.parse("20/10/2018 12:00:55", formatter);
        endDateTime = LocalDateTime.parse("20/10/2018 20:00:55", formatter);
    }


    @Test
    public void shouldThrowExceptionWhenNoDataFileFound() {

        Assertions.assertThrows(
                FileNotFoundException.class, () -> bankFileReader.findRawBatch(startDateTime, endDateTime, "testTransaction.csv", "ACC334455"));
    }

    @Test
    public void shouldReturnZeroRecordsWhenNoAccountIDFound() {

        try {
            responseModel = bankFileReader.findRawBatch(startDateTime, endDateTime, resourceDirectory.toFile().getAbsolutePath()+"/testTransaction.csv", "NO-ACCOUNT");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BigDecimal total = responseModel.getAccountBalance();
        assertEquals(0, responseModel.getNoOfTransactions());
        assertEquals(new BigDecimal("00"), total);
    }

    @Test
    public void shouldReturnValidResponseWhenRecordsFound() {
        try {
            responseModel = bankFileReader.findRawBatch(startDateTime, endDateTime, resourceDirectory.toFile().getAbsolutePath()+"/testTransaction.csv", "ACC334455");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BigDecimal total = responseModel.getAccountBalance();
        assertEquals(1, responseModel.getNoOfTransactions());
        assertEquals(new BigDecimal("-25.00"), total);
    }

    @Test
    public void shouldFindRecordsForDateRange() {
        LocalDateTime startDateTime = LocalDateTime.parse("19/10/2018 12:00:55", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("21/10/2018 20:00:55", formatter);

        try {
            responseModel = bankFileReader.findRawBatch(startDateTime, endDateTime, resourceDirectory.toFile().getAbsolutePath()+"/testTransaction.csv", "ACC334455");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BigDecimal total = responseModel.getAccountBalance();
        assertEquals(3, responseModel.getNoOfTransactions());
        assertEquals(new BigDecimal("-57.25"), total);
    }
}
