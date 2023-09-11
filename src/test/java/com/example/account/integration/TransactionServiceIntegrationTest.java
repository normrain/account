package com.example.account.integration;

import com.example.account.utils.PostgresTestContainer;
import com.example.account.utils.RabbitTestContainer;
import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.domain.transactions.entity.Transaction;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.domain.transactions.model.TransactionResponse;
import com.example.account.domain.transactions.repository.TransactionRepository;
import com.example.account.domain.transactions.service.TransactionService;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import com.example.account.util.exception.EntityNotFoundException;
import com.example.account.util.exception.InvalidBalanceException;
import com.example.account.service.RabbitMqSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer<PostgresTestContainer> postgreSQLContainer = PostgresTestContainer.getInstance();

    @ClassRule
    public static RabbitTestContainer rabbitTestContainer = RabbitTestContainer.getInstance();

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RabbitMqSenderService rabbitMqSenderService;

    @Test
    public void testCreateTransaction() throws InvalidBalanceException, EntityNotFoundException, JsonProcessingException {
        UUID accountId = UUID.randomUUID();
        AccountResponse account = accountService.createAccountAndBalances(
                new AccountRequest(
                        1001L,
                        "NL",
                        List.of(Currency.USD, Currency.EUR)
                )
        );
        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Test Transaction");

        TransactionResponse response = transactionService.createTransaction(account.accountId(), transactionRequest);

        Transaction transaction = transactionRepository.findById(response.id());

        assertNotNull(response);
        assertNotNull(transaction);

    }

    @Test(expected = EntityNotFoundException.class)
    public void testCreateTransactionNotExist() throws InvalidBalanceException, EntityNotFoundException, JsonProcessingException {
        UUID accountId = UUID.randomUUID();

        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Test Transaction");

        transactionService.createTransaction(accountId, transactionRequest);

    }

    @Test
    public void testGetTransactionsForAccount() throws EntityNotFoundException, JsonProcessingException {
        AccountResponse account = accountService.createAccountAndBalances(
                new AccountRequest(
                        1001L,
                        "NL",
                        List.of(Currency.USD, Currency.EUR)
                )
        );
        Transaction transaction1 = new Transaction(UUID.randomUUID(), account.accountId(), BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Transaction 1");
        Transaction transaction2 = new Transaction(UUID.randomUUID(), account.accountId(), BigDecimal.valueOf(30.00), Currency.EUR, Direction.OUT, "Transaction 2");
        transactionRepository.insert(transaction1);
        transactionRepository.insert(transaction2);

        List<TransactionResponse> responses = transactionService.getTransactionsForAccount(account.accountId());

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test(expected = EntityNotFoundException.class)
    public void accountNotexist() throws EntityNotFoundException, JsonProcessingException {
        UUID accountId = UUID.randomUUID();

        List<TransactionResponse> responses = transactionService.getTransactionsForAccount(accountId);
    }
}
