package com.example.account.domain.accounts.api.model;


import com.example.account.util.enums.Currency;
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
        @NotNull
        @Min(1)
        Long customerId,
        @NotNull
        @Size(max = 2, message = "Please use 2-character country code")
        String country,
        @NotNull
        List<Currency> currencies
) {}
