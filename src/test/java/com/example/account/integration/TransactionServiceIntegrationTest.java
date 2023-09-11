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
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


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
    public void withValidTransactionRequest_createTransaction() throws InvalidBalanceException, EntityNotFoundException, JsonProcessingException {
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

        assertNotNull(response.id());
        assertEquals(account.accountId(), response.accountId());
        assertEquals(BigDecimal.valueOf(50.0), response.amount());
        assertEquals(Currency.USD, response.currency());
        assertEquals(Direction.IN, response.direction());
        assertEquals("Test Transaction", response.description());
        assertEquals(BigDecimal.valueOf(50.00).setScale(2, RoundingMode.HALF_UP), response.newBalance());

        assertNotNull(transaction.getId());
        assertEquals(account.accountId(), transaction.getAccountId());
        assertEquals(BigDecimal.valueOf(50.0), transaction.getAmount());
        assertEquals(Currency.USD, transaction.getCurrency());
        assertEquals(Direction.IN, transaction.getDirection());
        assertEquals("Test Transaction", transaction.getDescription());
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenAccountDoesNotExistForCreation_throwException() throws InvalidBalanceException, EntityNotFoundException, JsonProcessingException {
        UUID accountId = UUID.randomUUID();

        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Test Transaction");

        transactionService.createTransaction(accountId, transactionRequest);
    }

    @Test
    public void withValidAccountAndExistingTransactions_fetchTransactions() throws EntityNotFoundException, JsonProcessingException {
        AccountResponse account = accountService.createAccountAndBalances(
                new AccountRequest(
                        1001L,
                        "NL",
                        List.of(Currency.USD, Currency.EUR)
                )
        );
        Transaction transaction1 = new Transaction(null, account.accountId(), BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Transaction 1");
        Transaction transaction2 = new Transaction(null, account.accountId(), BigDecimal.valueOf(30.00), Currency.EUR, Direction.OUT, "Transaction 2");
        transactionRepository.insert(transaction1);
        transactionRepository.insert(transaction2);

        List<TransactionResponse> response = transactionService.getTransactionsForAccount(account.accountId());

        assertEquals(2, response.size());

        assertNotNull(response.get(0).id());
        assertEquals(account.accountId(), response.get(0).accountId());
        assertEquals(BigDecimal.valueOf(50.0), response.get(0).amount());
        assertEquals(Currency.USD, response.get(0).currency());
        assertEquals(Direction.IN, response.get(0).direction());
        assertEquals("Transaction 1", response.get(0).description());
        assertNull(response.get(0).newBalance());

        assertNotNull(response.get(1).id());
        assertEquals(account.accountId(), response.get(1).accountId());
        assertEquals(BigDecimal.valueOf(30.0), response.get(1).amount());
        assertEquals(Currency.EUR, response.get(1).currency());
        assertEquals(Direction.OUT, response.get(1).direction());
        assertEquals("Transaction 2", response.get(1).description());
        assertNull(response.get(1).newBalance());

    }

    @Test(expected = EntityNotFoundException.class)
    public void whenAccountDoesNotExistForFetching_throwException() throws EntityNotFoundException, JsonProcessingException {
        UUID accountId = UUID.randomUUID();

        transactionService.getTransactionsForAccount(accountId);
    }
}
