package com.example.account.domain.accounts.api.controller;


import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.domain.transactions.model.TransactionResponse;
import com.example.account.domain.transactions.service.TransactionService;
import com.example.account.util.exception.EntityNotFoundException;
import com.example.account.util.exception.InvalidBalanceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/account")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping(value = "/create")
    @Operation(summary = "Endpoint to create a new account with balances in given currencies")
    public AccountResponse createAccount(@RequestBody @Valid AccountRequest accountRequest) throws JsonProcessingException {
        return accountService.createAccountAndBalances(accountRequest);
    }

    @PostMapping("/{accountId}/transactions/create")
    @Operation(summary = "Endpoint to create a new transaction for specified account")
    public TransactionResponse createTransaction(
            @PathVariable UUID accountId,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) throws InvalidBalanceException, EntityNotFoundException {
        return transactionService.createTransaction(accountId, transactionRequest);
    }

    @GetMapping(value = "/{accountId}")
    @Operation(summary = "Endpoint to fetch account data")
    public AccountResponse getAccount(@PathVariable @Valid UUID accountId) throws EntityNotFoundException {
        return accountService.getAccountWithBalances(accountId);
    }

    @GetMapping(value = "/{accountId}/transactions")
    @Operation(summary = "Endpoint to fetch transaction data for specified account")
    public List<TransactionResponse> getTransactions(@PathVariable @Valid UUID accountId) throws EntityNotFoundException {
        return transactionService.getTransactionsForAccount(accountId);
    }

}
