package com.example.account.domain.balances.service;


import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.repository.BalanceRepository;
import com.example.account.entity.Currency;
import com.example.account.entity.Direction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public Balance createBalance(Balance balance) {
        balanceRepository.insert(balance);
        return balanceRepository.findById(balance.getId());
    }

    public List<Balance> getBalancesForAccount(UUID accountId) {
        return balanceRepository.findByBalanceId(accountId);
    }

    public void updateBalanceAmount(UUID id, BigDecimal amount) {
        balanceRepository.updateAmount(id, amount);
    }

    public Balance findBalanceForAccountWithCurrency(UUID accountId, Currency currency) {
        return balanceRepository.findByAccountIdAndCurrency(accountId, currency);
    }
}
