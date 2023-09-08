package com.example.account.domain.transactions.api.controller;

import com.example.account.domain.transactions.api.model.TransactionRequest;
import com.example.account.domain.transactions.api.model.TransactionResponse;
import com.example.account.domain.transactions.command.CreateTransactionCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/transaction")
public class TransactionController {

    private final CreateTransactionCommand createTransactionCommand;
    @PostMapping(value = "/create")
    public TransactionResponse createTransaction(@RequestBody @Valid TransactionRequest transactionRequest) {
        return createTransactionCommand.execute(transactionRequest);
    }
}
