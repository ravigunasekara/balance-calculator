package org.me.calculator.model;

import java.math.BigDecimal;
import java.util.Collection;

public class ResponseModel {
    BigDecimal accountBalance;
    int noOfTransactions;

    public ResponseModel(Collection<BigDecimal> amounts) {
        this.accountBalance = amounts.stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
        this.noOfTransactions = amounts.size();
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public int getNoOfTransactions() {
        return noOfTransactions;
    }
}
