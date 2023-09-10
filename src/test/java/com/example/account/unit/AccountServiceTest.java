package com.example.account.unit;

import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.repository.AccountRepository;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.EventType;
import com.example.account.util.exception.EntityNotFoundException;
import com.example.account.service.RabbitMqSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private RabbitMqSenderService rabbitMqSenderService;

    @Test
    public void testCreateAccountAndBalances() throws JsonProcessingException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        String country = "EE";
        UUID accountID = UUID.randomUUID();

        // Arrange
        AccountRequest accountRequest = new AccountRequest(customerId, country, List.of(Currency.USD, Currency.EUR));

        // Mock the behavior of dependencies
        Account newAccount = new Account(accountID, country, customerId);

        List<BalanceResponse> expectedBalances = Arrays.asList(
                new BalanceResponse(BigDecimal.ZERO, Currency.USD),
                new BalanceResponse(BigDecimal.ZERO, Currency.EUR)
        );
        when(balanceService.getBalancesForAccount(any())).thenReturn(expectedBalances);

        // Act
        AccountResponse response = accountService.createAccountAndBalances(accountRequest);
        System.out.println(response);
        System.out.println(newAccount);

        // Assert
        assertEquals(newAccount.getId(), response.accountId());
        assertEquals(newAccount.getCustomerId(), response.customerId());
        assertEquals(expectedBalances, response.balances());

        // Verify that the sendMessageToQueue method was called once with the correct arguments
        verify(rabbitMqSenderService, times(1)).sendMessageToQueue(newAccount.getId(), EventType.CREATION);
    }

    @Test
    public void testGetAccountWithBalances() throws EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        String country = "EE";

        // Mock the behavior of dependencies
        Account mockAccount = new Account(accountId, country, customerId);
        when(accountRepository.findById(accountId)).thenReturn(mockAccount);

        List<BalanceResponse> expectedBalances = Arrays.asList(
                new BalanceResponse(BigDecimal.ZERO, Currency.USD),
                new BalanceResponse(BigDecimal.ZERO, Currency.EUR)
        );
        when(balanceService.getBalancesForAccount(accountId)).thenReturn(expectedBalances);

        // Act
        AccountResponse response = accountService.getAccountWithBalances(accountId);
        System.out.println(response);
        // Assert
        assertNotNull(response);
        assertEquals(accountId, response.accountId());
        assertEquals(customerId, response.customerId());
        assertEquals(expectedBalances, response.balances());

        // Verify that findById and getBalancesForAccount were called once
        verify(accountRepository, times(1)).findById(accountId);
        verify(balanceService, times(1)).getBalancesForAccount(accountId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetAccountWithBalancesEntityNotFound() throws EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();

        // Mock behavior of accountRepository to return null
        when(accountRepository.findById(accountId)).thenReturn(null);

        // Act and Assert
        accountService.getAccountWithBalances(accountId);

    }
}
