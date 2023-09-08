package com.example.account.domain.accounts.command;

import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetAccountCommand {

    private final AccountService accountService;
    private final BalanceService balanceService;

    public AccountResponse execute(UUID id) {
        Account account = accountService.getAccount(id);
        List<Balance> balances = balanceService.getBalancesForAccount(id);
        return AccountResponse.builder()
                .accountId(account.getId())
                .customerId(account.getCustomerId())
                .balances(balances)
                .build();
    }
}
