package com.example.account.domain.balances.service;


import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.repository.BalanceRepository;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import com.example.account.util.enums.EventType;
import com.example.account.util.exception.InvalidBalanceException;
import com.example.account.service.RabbitMqSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final RabbitMqSenderService rabbitMqSenderService;

    public List<BalanceResponse> getBalancesForAccount(UUID accountId) {
        List<Balance> balances = balanceRepository.findByAccountId(accountId);
        return balances.stream().map(balance ->
                        BalanceResponse.builder()
                                .balance(balance.getBalance())
                                .currency(balance.getCurrency())
                                .build())
                .collect(Collectors.toList());
    }

    public void createBalancesForAccount(UUID accountId, List<Currency> currencies){
        List<Currency> distinctCurrencies = currencies.stream()
                .distinct()
                .toList();
        for (Currency currency : distinctCurrencies) {
            Balance newBalance = Balance.builder()
                    .accountId(accountId)
                    .currency(currency)
                    .build();
            balanceRepository.insert(newBalance);
            rabbitMqSenderService.sendMessageToQueue(newBalance.getId(), EventType.CREATION);
        }
    }

    public BigDecimal updateAccountBalance(UUID accountId, BigDecimal amount, Direction direction, Currency currency) throws InvalidBalanceException {
        Balance balance = balanceRepository.findByAccountIdAndCurrency(accountId, currency);
        if(balance == null) {
            throw new InvalidBalanceException("Balance does not exist", accountId, currency, null);
        }
        BigDecimal newBalanceAmount = BigDecimal.ZERO;

        if (direction == Direction.IN) {
            newBalanceAmount = balance.getBalance().add(amount).setScale(2, RoundingMode.HALF_UP);
        }
        if (direction == Direction.OUT) {
            newBalanceAmount = balance.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_UP);
        }
        if(newBalanceAmount.compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidBalanceException(
                    "Insufficient Funds",
                    accountId,
                    currency,
                    balance.getId()
            );
        }
        rabbitMqSenderService.sendMessageToQueue(balance.getId(), EventType.UPDATE);
        balanceRepository.updateBalance(balance.getId(), newBalanceAmount);

        return newBalanceAmount;
    }
}
