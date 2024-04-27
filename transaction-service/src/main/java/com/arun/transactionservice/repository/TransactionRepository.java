package com.arun.transactionservice.repository;


import com.arun.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccountIdOrderByTimestampDesc(Long accountId);
}

