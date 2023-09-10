package com.example.account.integration;

import com.example.account.PostgresTestContainer;
import com.example.account.RabbitTestContainer;
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
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
public class AccountServiceIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer<PostgresTestContainer> postgreSQLContainer = PostgresTestContainer.getInstance();
    @ClassRule
    public static RabbitTestContainer rabbitTestContainer = RabbitTestContainer.getInstance();

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceService balanceService;

    @MockBean
    private RabbitMqSenderService rabbitMqSenderService;

    @Test
    public void testCreateAccountAndBalances() throws JsonProcessingException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "NL", Arrays.asList(Currency.USD, Currency.EUR));

        AccountResponse response = accountService.createAccountAndBalances(accountRequest);

        Account account = accountRepository.findById(response.accountId());

        assertNotNull(response);
        assertNotNull(account);

        verify(rabbitMqSenderService, times(3)).sendMessageToQueue(any(), eq(EventType.CREATION));
    }

    @Test
    public void testGetAccountWithBalances() throws EntityNotFoundException {
        // Set up test data in the database using accountRepository
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        Account account = new Account(null, "NL", customerId);
        accountRepository.insert(account);

        AccountResponse response = accountService.getAccountWithBalances(account.getId());

        assertNotNull(response);
        assertEquals(response.accountId(), account.getId());
        assertEquals(response.customerId(),account.getCustomerId());
        assertEquals(response.balances(), List.of());
    }

    // Additional integration tests as needed for error cases, edge cases, etc.
}
