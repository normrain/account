package com.example.account.domain.balances.command;

import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetBalancesForAccountCommand {

    private final BalanceService balanceService;

    public List<BalanceResponse> execute(UUID accountId) {
        return balanceService.getBalancesForAccount(accountId).stream().map(balance ->
                BalanceResponse.builder()
                        .balance(balance.getBalance())
                        .currency(balance.getCurrency())
                        .build()
        ).collect(Collectors.toList());
    }
}
