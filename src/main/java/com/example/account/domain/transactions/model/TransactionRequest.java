package com.example.account.domain.transactions.model;

import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(
                description = "Transaction amount",
                example = "10.0"
        )
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal amount,
        @Schema(
                description = "Currency the transaction is in",
                example = "SEK"
        )
        @NotNull
        Currency currency,
        @Schema(
                description = "Direction of the transaction",
                example = "OUT"
        )
        @NotNull
        Direction direction,
        @Schema(
                description = "Description of the transaction",
                example = "Payment for services"
        )
        @NotNull
        @NotBlank
        String description

) {}
