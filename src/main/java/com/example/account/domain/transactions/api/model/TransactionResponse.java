package com.example.account.domain.transactions.api.model;

import com.example.account.entity.Currency;
import com.example.account.entity.Direction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(
        UUID accountID,
        UUID id,
        BigDecimal amount,
        Currency currency,
        Direction direction,
        String description,
        BigDecimal newBalance
) {}
