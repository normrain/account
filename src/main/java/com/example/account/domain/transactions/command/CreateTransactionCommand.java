package com.example.account.domain.transactions.command;

import com.example.account.domain.balances.command.UpdateBalanceAmountCommand;
import com.example.account.domain.transactions.api.model.TransactionRequest;
import com.example.account.domain.transactions.api.model.TransactionResponse;
import com.example.account.domain.transactions.entity.Transaction;
import com.example.account.domain.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CreateTransactionCommand {

    private final TransactionService transactionService;
    private final UpdateBalanceAmountCommand updateBalanceAmountCommand;
    public TransactionResponse execute(TransactionRequest transactionRequest) {
        Transaction createdTransaction = transactionService.createTransaction(
                Transaction.builder()
                        .accountId(transactionRequest.accountId())
                        .amount(transactionRequest.amount())
                        .currency(transactionRequest.currency())
                        .direction(transactionRequest.direction())
                        .description(transactionRequest.description())
                        .build()
        );

        BigDecimal newBalance = updateBalanceAmountCommand.execute(
                transactionRequest.accountId(),
                transactionRequest.amount(),
                transactionRequest.direction(),
                transactionRequest.currency()
        );

        return TransactionResponse.builder()
                .id(createdTransaction.getId())
                .accountId(createdTransaction.getAccountId())
                .amount(createdTransaction.getAmount())
                .currency(createdTransaction.getCurrency())
                .direction(createdTransaction.getDirection())
                .description(createdTransaction.getDescription())
                .newBalance(newBalance)
                .build();
    }
}
