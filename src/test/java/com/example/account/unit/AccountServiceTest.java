package com.example.account.unit;

import com.example.account.PostgresTestContainer;
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
        AccountRequest accountRequest = new AccountRequest(customerId, country, List.of(Currency.USD, Currency.EUR));

        Account newAccount = new Account(accountID, country, customerId);
        List<BalanceResponse> expectedBalances = Arrays.asList(
                new BalanceResponse(BigDecimal.ZERO, Currency.USD),
                new BalanceResponse(BigDecimal.ZERO, Currency.EUR)
        );
        when(balanceService.getBalancesForAccount(any())).thenReturn(expectedBalances);

        AccountResponse response = accountService.createAccountAndBalances(accountRequest);

        assertEquals(newAccount.getCustomerId(), response.customerId());
        assertEquals(expectedBalances, response.balances());

        // Verify that the sendMessageToQueue method was called once with the correct arguments
        verify(rabbitMqSenderService, times(1)).sendMessageToQueue(any(), eq(EventType.CREATION));
    }

    @Test
    public void testGetAccountWithBalances() throws EntityNotFoundException {
        UUID accountId = UUID.randomUUID();
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        String country = "EE";

        Account mockAccount = new Account(accountId, country, customerId);
        when(accountRepository.findById(accountId)).thenReturn(mockAccount);

        List<BalanceResponse> expectedBalances = Arrays.asList(
                new BalanceResponse(BigDecimal.ZERO, Currency.USD),
                new BalanceResponse(BigDecimal.ZERO, Currency.EUR)
        );
        when(balanceService.getBalancesForAccount(accountId)).thenReturn(expectedBalances);

        AccountResponse response = accountService.getAccountWithBalances(accountId);

        assertNotNull(response);
        assertEquals(accountId, response.accountId());
        assertEquals(customerId, response.customerId());
        assertEquals(expectedBalances, response.balances());

        verify(accountRepository, times(1)).findById(accountId);
        verify(balanceService, times(1)).getBalancesForAccount(accountId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetAccountWithBalancesEntityNotFound() throws EntityNotFoundException {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(null);

        accountService.getAccountWithBalances(accountId);
    }
}
