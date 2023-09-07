package com.example.account.domain.accounts.api.model;

import com.example.account.domain.balances.entity.Balance;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Jacksonized
@Builder
public record AccountCreationResponse(
        UUID accountId,
        UUID customerId,
        List<Balance> balances
) {}
