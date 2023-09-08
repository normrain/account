package com.example.account.domain.transactions.service;

import com.example.account.domain.transactions.entity.Transaction;
import com.example.account.domain.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction createTransaction(Transaction transaction) {
        transactionRepository.insert(transaction);
        return transactionRepository.findById(transaction.getId());
    }

    public List<Transaction> getTransactionsForAccount(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}
