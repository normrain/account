package com.example.account.domain.balances.command;

import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.entity.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateBalancesForAccountCommand {

    private final BalanceService balanceService;

    public void execute(UUID accountId, List<Currency> currencies) {
        for(Currency currency : currencies) {
            balanceService.createBalance(
                    Balance.builder()
                            .accountId(accountId)
                            .currency(currency)
                            .build()
            );
        }

    }
}
