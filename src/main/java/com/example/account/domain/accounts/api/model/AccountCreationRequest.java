package com.example.account.domain.accounts.api.model;


import com.example.account.entity.Currency;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
@Jacksonized
@Builder
public record AccountCreationRequest(
        @NonNull
        UUID customerId,
        @NonNull
        String country,
        @NonNull
        List<Currency> currencies
) {}
