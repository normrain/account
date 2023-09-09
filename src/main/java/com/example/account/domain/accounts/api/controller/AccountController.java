package com.example.account.domain.accounts.api.controller;


import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.command.CreateAccountAndBalancesCommand;
import com.example.account.domain.accounts.command.GetAccountCommand;
import com.example.account.domain.transactions.api.model.TransactionResponse;
import com.example.account.domain.transactions.command.GetTransactionsForAccountCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/account")
public class AccountController {

    private final CreateAccountAndBalancesCommand createAccountAndBalancesCommand;
    private final GetAccountCommand getAccountCommand;
    private final GetTransactionsForAccountCommand getTransactionsForAccountCommand;

    @PostMapping(value = "/create")
    public AccountResponse createAccount(@RequestBody @Valid AccountRequest accountRequest){
        return createAccountAndBalancesCommand.execute(accountRequest);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID id) {
        return new ResponseEntity<>(getAccountCommand.execute(id), HttpStatus.valueOf(200));
    }

    @GetMapping(value = "/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable UUID accountId) {
        return getTransactionsForAccountCommand.execute(accountId);
    }
}
