package com.example.account.domain.accounts.service;

import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(Account account){
        accountRepository.insert(account);
        return accountRepository.findById(account.getId());
    }
}
