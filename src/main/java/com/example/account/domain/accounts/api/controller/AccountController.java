package com.example.account.domain.accounts.api.controller;


import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.domain.transactions.model.TransactionResponse;
import com.example.account.domain.transactions.service.TransactionService;
import com.example.account.exception.AccountNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public AccountResponse createAccount(@RequestBody @Valid AccountRequest accountRequest) throws JsonProcessingException {
        return accountService.createAccountAndBalances(accountRequest);
    }

    @PostMapping("/{accountId}/transactions/create")
    public TransactionResponse createTransaction(@PathVariable UUID accountId, @RequestBody @Valid TransactionRequest transactionRequest){
        return transactionService.createTransaction(accountId, transactionRequest);
    }

    @GetMapping(value = "/{accountId}")
    public AccountResponse getAccount(@PathVariable UUID accountId) {
        return accountService.getAccountWithBalances(accountId);
    }

    @GetMapping(value = "/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable UUID accountId) {
        return transactionService.getTransactionsForAccount(accountId);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(
                ex.getMessage()
        );
    }
}
