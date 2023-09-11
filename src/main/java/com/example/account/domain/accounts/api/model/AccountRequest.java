package com.example.account.domain.accounts.api.model;


import com.example.account.util.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Jacksonized
@Builder
public record AccountRequest(
        @Schema(
                description = "Customer number",
                example = "101"
        )
        @NotNull
        @Min(1)
        Long customerId,
        @Schema(
                description = "Country the account should be in (2-character country code)",
                example = "NL"
        )
        @NotNull
        @Size(max = 2, message = "Please use 2-character country code")
        String country,
        @Schema(
                description = "List of currencies, balanes will be created for (N.B. duplicates will be ignores)",
                example = "[SEK, EUR]"
        )
        @NotNull
        List<Currency> currencies
) {}
