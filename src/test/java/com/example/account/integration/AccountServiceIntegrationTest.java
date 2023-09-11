package com.example.account.integration;

import com.example.account.utils.PostgresTestContainer;
import com.example.account.utils.RabbitTestContainer;
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
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

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
@ActiveProfiles("test")
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
    public void withValidAccountRequest_createsAccounts() throws JsonProcessingException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "NL", List.of(Currency.USD, Currency.EUR));

        AccountResponse response = accountService.createAccountAndBalances(accountRequest);

        Account account = accountRepository.findById(response.accountId());

        assertNotNull(response);
        assertNotNull(account);

        verify(rabbitMqSenderService, times(3)).sendMessageToQueue(any(), eq(EventType.CREATION));
    }

    @Test
    public void withDuplicateCurrency_onlyCreatesOneBalanceForCurrency() throws JsonProcessingException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "NL", List.of(Currency.EUR, Currency.EUR));

        AccountResponse response = accountService.createAccountAndBalances(accountRequest);

        Account account = accountRepository.findById(response.accountId());
        List<BalanceResponse> balances = balanceService.getBalancesForAccount(response.accountId());

        assertNotNull(response);
        assertNotNull(account);
        assertEquals(1, balances.size());

        verify(rabbitMqSenderService, times(2)).sendMessageToQueue(any(), eq(EventType.CREATION));
    }

    @Test
    public void withExistingAccount_returnsAccount() throws EntityNotFoundException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        Account account = new Account(null, "NL", customerId);
        accountRepository.insert(account);

        AccountResponse response = accountService.getAccountWithBalances(account.getId());

        assertNotNull(response);
        assertEquals(response.accountId(), account.getId());
        assertEquals(response.customerId(),account.getCustomerId());
        assertEquals(response.balances(), List.of());
    }

    @Test(expected = EntityNotFoundException.class)
    public void withNotExistingAccount_throwsException() throws EntityNotFoundException {
        UUID accountId = UUID.randomUUID();

        accountService.getAccountWithBalances(accountId);

    }
}
