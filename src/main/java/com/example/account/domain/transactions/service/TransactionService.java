package com.example.account.domain.transactions.service;

import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.domain.transactions.model.TransactionResponse;
import com.example.account.domain.transactions.entity.Transaction;
import com.example.account.domain.transactions.repository.TransactionRepository;
import com.example.account.entity.EventType;
import com.example.account.exception.EntityNotFoundException;
import com.example.account.exception.InvalidBalanceException;
import com.example.account.service.RabbitMqSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final AccountService accountService;
    private final RabbitMqSenderService rabbitMqSenderService;

    public TransactionResponse createTransaction(UUID accountId, TransactionRequest transactionRequest) throws InvalidBalanceException, EntityNotFoundException {
        accountService.getAccountWithBalances(accountId);

        Transaction newTransaction = Transaction.builder()
                .accountId(accountId)
                .amount(transactionRequest.amount())
                .currency(transactionRequest.currency())
                .direction(transactionRequest.direction())
                .description(transactionRequest.description())
                .build();

        transactionRepository.insert(newTransaction);

        rabbitMqSenderService.sendMessageToQueue(newTransaction.getId(), EventType.CREATION);

        BigDecimal newBalance = balanceService.updateAccountBalance(
                accountId,
                transactionRequest.amount(),
                transactionRequest.direction(),
                transactionRequest.currency()
        );

        return TransactionResponse.builder()
                .id(newTransaction.getId())
                .accountId(newTransaction.getAccountId())
                .amount(newTransaction.getAmount())
                .currency(newTransaction.getCurrency())
                .direction(newTransaction.getDirection())
                .description(newTransaction.getDescription())
                .newBalance(newBalance)
                .build();
    }

    public List<TransactionResponse> getTransactionsForAccount(UUID accountId) throws EntityNotFoundException {
        accountService.getAccountWithBalances(accountId);

        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .map( transaction -> TransactionResponse.builder()
                        .id(transaction.getId())
                        .currency(transaction.getCurrency())
                        .accountId(transaction.getAccountId())
                        .amount(transaction.getAmount())
                        .direction(transaction.getDirection())
                        .description(transaction.getDescription())
                        .newBalance(null).build()
                ).collect(Collectors.toList());
    }
}
