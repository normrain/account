package com.example.account.domain.transactions.api.model;

import com.example.account.entity.Currency;
import com.example.account.entity.Direction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
        UUID accountId,
        BigDecimal amount,
        Currency currency,
        Direction direction,
        String description

) {}
