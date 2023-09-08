package com.example.account.domain.transactions.command;

import com.example.account.domain.transactions.api.model.TransactionResponse;
import com.example.account.domain.transactions.entity.Transaction;
import com.example.account.domain.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetTransactionsForAccountCommand {

    private final TransactionService transactionService;

    public List<TransactionResponse> execute(UUID accountId) {
        List<Transaction> transactions = transactionService.getTransactionsForAccount(accountId);
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
