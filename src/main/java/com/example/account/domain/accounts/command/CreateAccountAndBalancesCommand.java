package com.example.account.domain.accounts.command;


import com.example.account.domain.accounts.api.model.AccountCreationRequest;
import com.example.account.domain.accounts.api.model.AccountCreationResponse;
import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.entity.Balance;
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


    public AccountCreationResponse execute(AccountCreationRequest accountCreationRequest) {
        Account createdAccount = accountService.createAccount(
                Account.builder()
                        .customerId(accountCreationRequest.customerId())
                        .country(accountCreationRequest.country())
                        .build()
        );
        List<Balance> createdBalances = new ArrayList<>();
        for(Currency currency : accountCreationRequest.currencies()) {
            createdBalances.add(balanceService.createBalance(
                    Balance.builder()
                            .accountId(createdAccount.getId())
                            .currency(currency)
                            .build()
            ));
        }

        return AccountCreationResponse.builder()
                .accountId(createdAccount.getId())
                .customerId(createdAccount.getCustomerId())
                .balances(createdBalances)
                .build();
    }
}
