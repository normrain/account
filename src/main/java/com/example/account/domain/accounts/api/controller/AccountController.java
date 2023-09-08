package com.example.account.domain.accounts.api.controller;


import com.example.account.domain.accounts.api.model.AccountCreationRequest;
import com.example.account.domain.accounts.api.model.AccountCreationResponse;
import com.example.account.domain.accounts.command.CreateAccountAndBalancesCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.annotation.*;

@Slf4j
@Validated
@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/account")
public class AccountController {

    private final CreateAccountAndBalancesCommand createAccountAndBalancesCommand;

    @PostMapping(value = "/create")
    public AccountCreationResponse createAccount(@RequestBody @Valid AccountCreationRequest accountCreationRequest){
        return createAccountAndBalancesCommand.execute(accountCreationRequest);
    }
}
