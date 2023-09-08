package com.example.account.domain.accounts.api.model;


import com.example.account.entity.Currency;
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
        Long customerId,
        @NonNull
        String country,
        @NonNull
        List<Currency> currencies
) {}
