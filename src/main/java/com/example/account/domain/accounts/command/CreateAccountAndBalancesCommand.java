package com.example.account.domain.accounts.command;


import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.command.GetBalancesForAccountCommand;
import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.entity.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateAccountAndBalancesCommand {

    private final AccountService accountService;
    private final BalanceService balanceService;
    private final GetBalancesForAccountCommand getBalancesForAccountCommand;

    public AccountResponse execute(AccountRequest accountRequest) {
        Account createdAccount = accountService.createAccount(
                Account.builder()
                        .customerId(accountRequest.customerId())
                        .country(accountRequest.country())
                        .build()
        );
        for(Currency currency : accountRequest.currencies()) {
            balanceService.createBalance(
                    Balance.builder()
                            .accountId(createdAccount.getId())
                            .currency(currency)
                            .build()
            );
        }

        List<BalanceResponse> balances = getBalancesForAccountCommand.execute(createdAccount.getId());
        return AccountResponse.builder()
                .accountId(createdAccount.getId())
                .customerId(createdAccount.getCustomerId())
                .balances(balances)
                .build();
    }
}
