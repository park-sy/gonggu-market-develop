package com.gonggu.pay.response;

import com.gonggu.pay.domain.Transaction;
import lombok.Getter;

@Getter
public class TransactionResponse {

    private final Long id;
    private final String fromName;
    private final String toName;
    private final Long amount;
    private final String time;

    public TransactionResponse(Transaction transaction){
        this.id = transaction.getId();
        this.fromName = transaction.getFrom().getName();
        this.toName = transaction.getTo().getName();
        this.amount = transaction.getAmount();
        this.time = transaction.getDate().toString();
    }
}
