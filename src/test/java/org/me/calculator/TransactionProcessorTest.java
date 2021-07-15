package org.me.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionProcessorTest {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    Map<Integer, List<String>> columnValues;
    private LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    TransactionProcessor transactionProcessor;

    @BeforeEach
    void setUp() {
        columnValues = new HashMap<>();
        List<String> txnIdList = new ArrayList<String>(List.of("TX10001","TX10002","TX10003","TX10004","TX10005","TX10006"));
        List<String> fromAccIdList = new ArrayList<String>(List.of("ACC334455","ACC334455","ACC334455","ACC998877","ACC334455","ACC334455"));
        List<String> toAccIdList = new ArrayList<String>(List.of("ACC778899","ACC778899","ACC998877","ACC778899","ACC998877","ACC778899"));
        List<String> createdAtList = new ArrayList<String>(List.of("19/10/2018 12:47:55","20/10/2018 12:47:55","20/10/2018 17:33:43","20/10/2018 18:00:00","20/10/2018 19:45:00","21/10/2018 09:30:00"));
        List<String> amountList = new ArrayList<String>(List.of("25.00","25.00","10.50","5.00","25.00","7.25"));
        List<String> txnTypeList = new ArrayList<String>(List.of("PAYMENT","PAYMENT","PAYMENT","PAYMENT","REVERSAL","PAYMENT"));
        List<String> reverseTaxIdAtdList = new ArrayList<String>(List.of("","","","","TX10002",""));
        columnValues.put(0, txnIdList);
        columnValues.put(1, fromAccIdList);
        columnValues.put(2, toAccIdList);
        columnValues.put(3, createdAtList);
        columnValues.put(4, amountList);
        columnValues.put(5, txnTypeList);
        columnValues.put(6, reverseTaxIdAtdList);
        startDateTime = LocalDateTime.parse("19/10/2018 12:00:55", formatter);
        endDateTime = LocalDateTime.parse("20/10/2018 20:00:55", formatter);
        Map<String, BigDecimal> selectedTxn = new HashMap<>();
        transactionProcessor = new TransactionProcessor(selectedTxn);
    }


    @Test
    void shouldReturnEmptyMapWhenNoAccountID() {
        Map<String, BigDecimal> map = transactionProcessor.processRecords(columnValues, startDateTime, endDateTime, "NO-ID");

        assertEquals(0, map.size());
    }

    @Test
    void shouldFindValidTransactions() {
        Map<String, BigDecimal> map = transactionProcessor.processRecords(columnValues, startDateTime, endDateTime, "ACC334455");

        assertEquals(2, map.size());
        assertEquals(new BigDecimal("-35.50"), map.values().stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b)));

    }

    @Test
    void shouldNotSelectIfTxnReverseAfterSelectedPeriod() {
        //period is from 19th to 20th Oct 2018
        columnValues.get(0).add("TX10007");
        columnValues.get(3).add("21/10/2020 09:30:00");
        columnValues.get(5).add("REVERSAL");
        columnValues.get(6).add("TX10001");

        Map<String, BigDecimal> map = transactionProcessor.processRecords(columnValues, startDateTime, endDateTime, "ACC334455");
        assertEquals(1, map.size());
        assertEquals(new BigDecimal("-10.50"), map.values().stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b)));
    }

    @Test
    void shouldIgnoreReversalsForTxnHappenedBeforeStartTime() {
        List<String> reverseTaxIdAtdList = new ArrayList<String>(List.of("","TX10000","","","",""));
        List<String> txnTypeList = new ArrayList<String>(List.of("PAYMENT","REVERSAL","PAYMENT","PAYMENT","PAYMENT","PAYMENT"));
        columnValues.put(5, txnTypeList);
        columnValues.put(6, reverseTaxIdAtdList);
        Map<String, BigDecimal> map = transactionProcessor.processRecords(columnValues, startDateTime, endDateTime, "ACC334455");

        assertEquals(3, map.size());
        assertEquals(new BigDecimal("-60.50"), map.values().stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b)));
    }


}