package org.me.calculator.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseModelTest {

    @Test
    void shouldReturnValidResponse() {
        List<BigDecimal> collection = new ArrayList<>();
        collection.add(BigDecimal.ONE);
        collection.add(BigDecimal.TEN);

        ResponseModel model = new ResponseModel(collection);
        assertEquals(2, model.getNoOfTransactions());
        assertEquals(new BigDecimal(11), model.getAccountBalance());
    }

    @Test
    void shouldReturnZeroTransactionsForEmptyOrNullInput() {
        Map<String, BigDecimal> selectedTxn = new HashMap<>();

        ResponseModel model = new ResponseModel(null);
        assertEquals(0, model.getNoOfTransactions());
        assertEquals(new BigDecimal(0), model.getAccountBalance());
    }
}