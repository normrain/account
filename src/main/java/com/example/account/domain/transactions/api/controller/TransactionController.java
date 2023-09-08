package com.example.account.domain.transactions.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/transaction")
public class TransactionController {


}
