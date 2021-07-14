package org.me.calculator;

import com.univocity.parsers.common.processor.BatchedColumnProcessor;
import com.univocity.parsers.common.processor.core.AbstractBatchedColumnProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankFileReaderIntegrationTest {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private Path resourceDirectory = Paths.get("src","test","resources");

    Map<String, BigDecimal> selectedTxn = new HashMap<>();
    BankFileReader bankFileReader = new BankFileReader(selectedTxn);

    @Test
    public void shouldThrowExceptionWhenNoDataFileFound() {
        LocalDateTime startDateTime = LocalDateTime.parse("20/10/2018 12:00:55", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("20/10/2018 20:00:55", formatter);

        Assertions.assertThrows(
                FileNotFoundException.class, () -> bankFileReader.findRawBatch(startDateTime, endDateTime, "testTransaction.csv", "ACC334455"));
    }

    @Test
    public void shouldReturnZeroRecordsWhenNoAccountIDFound() {
        LocalDateTime startDateTime = LocalDateTime.parse("20/10/2018 12:00:55", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("20/10/2018 20:00:55", formatter);

        try {
            selectedTxn = bankFileReader.findRawBatch(startDateTime, endDateTime, resourceDirectory.toFile().getAbsolutePath()+"/testTransaction.csv", "NO-ACCOUNT");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BigDecimal total = selectedTxn.values().stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
        assertEquals(0, selectedTxn.size());
        assertEquals(new BigDecimal("00"), total);
    }

    @Test
    public void shouldReturnValidResponseWhenRecordsFound() {
        LocalDateTime startDateTime = LocalDateTime.parse("20/10/2018 12:00:55", formatter);
        LocalDateTime endDateTime = LocalDateTime.parse("20/10/2018 20:00:55", formatter);

        try {
            selectedTxn = bankFileReader.findRawBatch(startDateTime, endDateTime, resourceDirectory.toFile().getAbsolutePath()+"/testTransaction.csv", "ACC334455");
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
