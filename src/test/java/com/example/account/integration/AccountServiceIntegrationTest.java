package com.example.account.integration;

import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.entity.Account;
import com.example.account.domain.accounts.repository.AccountRepository;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.EventType;
import com.example.account.util.exception.EntityNotFoundException;
import com.example.account.service.RabbitMqSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private RabbitMqSenderService rabbitMqSenderService;

    @Test
    public void testCreateAccountAndBalances() throws JsonProcessingException {
        // Set up test data
        AccountRequest accountRequest = new AccountRequest("customerId", "country", Arrays.asList(Currency.USD, Currency.EUR));

        // Call the method to be tested
        AccountResponse response = accountService.createAccountAndBalances(accountRequest);

        // Retrieve the account from the database using accountRepository
        Account account = accountRepository.findById(response.accountId());

        // Assert the results
        assertNotNull(response);
        assertNotNull(account);
        // Add more assertions as needed

        // Verify interactions with balanceService and rabbitMqSenderService using Mockito
        verify(balanceService, times(1)).createBalancesForAccount(any(UUID.class), anyList());
        verify(rabbitMqSenderService, times(1)).sendMessageToQueue(any(UUID.class), eq(EventType.CREATION));
    }

    @Test
    public void testGetAccountWithBalances() throws EntityNotFoundException {
        // Set up test data in the database using accountRepository
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, "customerId", "country");
        accountRepository.save(account);

        // Call the method to be tested
        AccountResponse response = accountService.getAccountWithBalances(accountId);

        // Assert the results
        assertNotNull(response);
        assertEquals(accountId, response.accountId());
        // Add more assertions as needed
    }

    // Additional integration tests as needed for error cases, edge cases, etc.
}
