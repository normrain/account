package com.example.account.domain.accounts.api.model;

import com.example.account.domain.balances.model.BalanceResponse;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Jacksonized
@Builder
public record AccountResponse(
        UUID accountId,
        Long customerId,
        List<BalanceResponse> balances
) {}
