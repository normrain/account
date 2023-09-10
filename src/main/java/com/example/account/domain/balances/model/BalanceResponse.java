package com.example.account.domain.balances.model;

import com.example.account.util.enums.Currency;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@Jacksonized
@Builder
public record BalanceResponse(
        BigDecimal balance,
        Currency currency
) {}
