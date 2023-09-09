package com.example.account.domain.accounts.command;


import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.command.CreateBalancesForAccountCommand;
import com.example.account.domain.balances.command.GetBalancesForAccountCommand;
import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.entity.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateAccountAndBalancesCommand {

    private final AccountService accountService;
    private final CreateBalancesForAccountCommand createBalancesForAccountCommand;
    private final GetBalancesForAccountCommand getBalancesForAccountCommand;
    private final AmqpTemplate rabbitTemplate;
    private final Queue queue;

    public AccountResponse execute(AccountRequest accountRequest) {
        Account createdAccount = accountService.createAccount(
                Account.builder()
                        .customerId(accountRequest.customerId())
                        .country(accountRequest.country())
                        .build()
        );
        createBalancesForAccountCommand.execute(createdAccount.getId(), accountRequest.currencies());
        rabbitTemplate.convertAndSend(queue.getName(), createdAccount.toString());
        List<BalanceResponse> balances = getBalancesForAccountCommand.execute(createdAccount.getId());
        return AccountResponse.builder()
                .accountId(createdAccount.getId())
                .customerId(createdAccount.getCustomerId())
                .balances(balances)
                .build();
    }
}
