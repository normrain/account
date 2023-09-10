package com.example.account.unit;

import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.repository.BalanceRepository;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import com.example.account.util.enums.EventType;
import com.example.account.util.exception.InvalidBalanceException;
import com.example.account.service.RabbitMqSenderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BalanceServiceTest {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private RabbitMqSenderService rabbitMqSenderService;

    @Test
    public void testGetBalancesForAccount() {
        UUID accountId = UUID.randomUUID();
        List<Balance> balances = Arrays.asList(
                new Balance(UUID.randomUUID(), accountId, BigDecimal.valueOf(100.00), Currency.USD),
                new Balance(UUID.randomUUID(), accountId, BigDecimal.valueOf(50.00), Currency.EUR)
        );

        when(balanceRepository.findByAccountId(accountId)).thenReturn(balances);

        List<BalanceResponse> result = balanceService.getBalancesForAccount(accountId);

        assertNotNull(result);
        assertEquals(balances.size(), result.size());
        assertEquals(Currency.USD, result.get(0).currency());
        assertEquals(BigDecimal.valueOf(100.00), result.get(0).balance());
        assertEquals(Currency.EUR, result.get(1).currency());
        assertEquals(BigDecimal.valueOf(50.00), result.get(1).balance());
    }

    @Test
    public void testCreateBalancesForAccount() {
        UUID accountId = UUID.randomUUID();
        List<Currency> currencies = List.of(Currency.USD, Currency.EUR);

        balanceService.createBalancesForAccount(accountId, currencies);

        verify(balanceRepository, times(2)).insert(any());
        verify(rabbitMqSenderService, times(2)).sendMessageToQueue(any(), eq(EventType.CREATION));
    }

    @Test
    public void testUpdateAccountBalance() throws InvalidBalanceException {
        UUID accountId = UUID.randomUUID();
        Currency currency = Currency.USD;
        Balance balance = new Balance(UUID.randomUUID(), accountId, BigDecimal.valueOf(100.00), currency);

        when(balanceRepository.findByAccountIdAndCurrency(accountId, currency)).thenReturn(balance);
        BigDecimal amount = BigDecimal.valueOf(50.00);

        BigDecimal newBalanceAmount = balanceService.updateAccountBalance(accountId, amount, Direction.IN, currency);

        assertNotNull(newBalanceAmount);
        assertEquals(BigDecimal.valueOf(150.00).setScale(2, RoundingMode.HALF_UP), newBalanceAmount);

        verify(rabbitMqSenderService, times(1)).sendMessageToQueue(balance.getId(), EventType.UPDATE);
        verify(balanceRepository, times(1)).updateBalance(balance.getId(), newBalanceAmount);
    }

    @Test(expected = InvalidBalanceException.class)
    public void testUpdateAccountBalanceInsufficientFunds() throws InvalidBalanceException {
        UUID accountId = UUID.randomUUID();
        Currency currency = Currency.USD;
        Balance balance = new Balance(UUID.randomUUID(), accountId, BigDecimal.valueOf(100.00), currency);

        when(balanceRepository.findByAccountIdAndCurrency(accountId, currency)).thenReturn(balance);

        BigDecimal amount = BigDecimal.valueOf(200.00);

        balanceService.updateAccountBalance(accountId, amount, Direction.OUT, currency);
    }
}
