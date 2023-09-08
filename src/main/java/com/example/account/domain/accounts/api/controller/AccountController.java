package com.example.account.domain.accounts.api.controller;


import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.command.CreateAccountAndBalancesCommand;
import com.example.account.domain.accounts.command.GetAccountCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping(value = "/create")
    public AccountResponse createAccount(@RequestBody @Valid AccountRequest accountRequest){
        return createAccountAndBalancesCommand.execute(accountRequest);
    }

    @GetMapping(value = "/{id}")
    public AccountResponse getAccount(@PathVariable UUID id) {
        return getAccountCommand.execute(id);
    }
}
