package com.gonggu.pay.repository;

import com.gonggu.pay.domain.Transaction;
import com.gonggu.pay.request.TransactionRequest;
import com.gonggu.pay.response.TransactionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {

}
