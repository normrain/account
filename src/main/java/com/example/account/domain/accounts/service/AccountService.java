package com.example.account.domain.accounts.service;

import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.repository.AccountRepository;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.entity.EventType;
import com.example.account.exception.EntityNotFoundException;
import com.example.account.service.RabbitMqSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountService {


    private final BalanceService balanceService;
    private final AccountRepository accountRepository;
    private final RabbitMqSenderService rabbitMqSenderService;

    public AccountResponse createAccountAndBalances(AccountRequest accountRequest) throws JsonProcessingException {

        Account newAccount = Account.builder()
                .customerId(accountRequest.customerId())
                .country(accountRequest.country())
                .build();

        accountRepository.insert(newAccount);

        balanceService.createBalancesForAccount(newAccount.getId(), accountRequest.currencies());

        List<BalanceResponse> balances = balanceService.getBalancesForAccount(newAccount.getId());

        AccountResponse response = AccountResponse.builder()
                .accountId(newAccount.getId())
                .customerId(newAccount.getCustomerId())
                .balances(balances)
                .build();

        rabbitMqSenderService.sendMessageToQueue(newAccount.getId(), EventType.CREATION);

        return response;
    }

    public AccountResponse getAccountWithBalances(UUID id) throws EntityNotFoundException {
        Account account = accountRepository.findById(id);
        List<BalanceResponse> balances = balanceService.getBalancesForAccount(id);

        if(account == null) {
            throw new EntityNotFoundException("Account", id);
        }

        return AccountResponse.builder()
                .accountId(account.getId())
                .customerId(account.getCustomerId())
                .balances(balances)
                .build();
    }
}
