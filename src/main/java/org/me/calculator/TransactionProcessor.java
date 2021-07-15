package org.me.calculator;

import org.me.calculator.model.TxnType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.me.calculator.common.DateFormatter.findValidDateTime;

public class TransactionProcessor {
    private Map<String, BigDecimal> selectedTxn;

    public TransactionProcessor(Map<String, BigDecimal> selectedTxn) {
        this.selectedTxn = selectedTxn;
    }

    public Map<String, BigDecimal> processRecords(Map<Integer, List<String>> columnValues,
                                                  LocalDateTime startDateTime,
                                                  LocalDateTime endDateTime,
                                                  String accountId) {
        List<String> rawValues = columnValues.get(3);

        int pointer = 0;
        for (String date : rawValues) {
            LocalDateTime recordDate = findValidDateTime(date);
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
        return selectedTxn;
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

            BigDecimal num;
            if (fromAccountIds.get(pointer).equals(accountId)) {
                num = new BigDecimal(amount.get(pointer));
                selectedTxn.put(txnIds.get(pointer), num.negate());
            }
            if (toAccountIds.get(pointer).equals(accountId)) {
                num = new BigDecimal(amount.get(pointer));
                selectedTxn.put(txnIds.get(pointer), num);
            }
        }
    }
}
