package com.example.account.domain.transactions.model;

import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@Jacksonized
@Builder
public record TransactionRequest(

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal amount,
        Currency currency,
        Direction direction,
        @NotNull
        @NotBlank
        String description

) {}
