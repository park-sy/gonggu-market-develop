package com.gonggu.pay.repository;

import com.gonggu.pay.domain.Transaction;
import com.gonggu.pay.domain.User;
import com.gonggu.pay.request.TransactionRequest;
import com.gonggu.pay.response.TransactionResponse;

import java.util.List;

public interface TransactionRepositoryCustom {
    List<Transaction> getList(User user, TransactionRequest transactionRequest);
}
