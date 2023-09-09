package com.example.account.domain.accounts.api.model;


import com.example.account.entity.Currency;
import com.example.account.validation.ValidateCurrencyList;
import com.example.account.validation.ValidateEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Jacksonized
@Builder
public record AccountRequest(
        @NonNull
        @Min(1)
        Long customerId,
        @NonNull
        @Size(max = 2, message = "Please use 2-character country code")
        String country,
        @NonNull
        List<Currency> currencies
) {}
