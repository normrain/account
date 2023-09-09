package com.example.account.domain.balances.command;

import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.entity.Currency;
import com.example.account.entity.Direction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateBalanceAmountCommand {
    private final BalanceService balanceService;

    public BigDecimal execute(UUID accountId, BigDecimal amount, Direction direction, Currency currency) {
        Balance balance = balanceService.findBalanceForAccountWithCurrency(accountId, currency);
        BigDecimal newBalance = BigDecimal.ZERO;

        if(direction==Direction.IN) {
            newBalance = balance.getBalance().add(amount);
        }
        if(direction == Direction.OUT) {
            newBalance = balance.getBalance().subtract(amount);
        }

        balanceService.updateBalanceAmount(balance.getId(), newBalance);

        return newBalance;
    }

}
