package com.example.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.UUID;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Account Not Found")
public class AccountNotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = -3332292346834265371L;

    public AccountNotFoundException(UUID id){
        super(String.format("Account with id=%s not found ", id.toString()));
    }
}
