package com.example.account.domain.transactions.model;

import com.example.account.entity.Currency;
import com.example.account.entity.Direction;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
        UUID accountId,
        UUID id,
        BigDecimal amount,
        Currency currency,
        Direction direction,
        String description,
        BigDecimal newBalance
) {}
