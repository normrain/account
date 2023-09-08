package com.example.account.domain.transactions.api.model;

import com.example.account.entity.Currency;
import com.example.account.entity.Direction;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.UUID;

@Validated
@Jacksonized
@Builder
public record TransactionRequest(
        UUID accountId,
        BigDecimal amount,
        Currency currency,
        Direction direction,
        String description

) {}
