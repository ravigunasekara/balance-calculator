package org.me.calculator.model;

import java.math.BigDecimal;
import java.util.Collection;

public class ResponseModel {
    private BigDecimal accountBalance  = BigDecimal.ZERO;
    private int noOfTransactions = 0;

    public ResponseModel(Collection<BigDecimal> amounts) {
        if(amounts != null) {
            this.accountBalance = amounts.stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
            this.noOfTransactions = amounts.size();
        }

    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public int getNoOfTransactions() {
        return noOfTransactions;
    }
}
