package com.example.account.integration;

import com.example.account.PostgresTestContainer;
import com.example.account.RabbitTestContainer;
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
import com.example.account.util.enums.EventType;
import com.example.account.util.exception.EntityNotFoundException;
import com.example.account.util.exception.InvalidBalanceException;
import com.example.account.service.RabbitMqSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        // Set up test data
        UUID accountId = UUID.randomUUID();
        AccountResponse account = accountService.createAccountAndBalances(
                new AccountRequest(
                        1001L,
                        "NL",
                        List.of(Currency.USD, Currency.EUR)
                )
        );
        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Test Transaction");

        // Call the method to be tested
        TransactionResponse response = transactionService.createTransaction(account.accountId(), transactionRequest);

        // Retrieve the transaction from the database using transactionRepository
        Transaction transaction = transactionRepository.findById(response.id());

        // Assert the results
        assertNotNull(response);
        assertNotNull(transaction);
        // Add more assertions as needed

    }

    @Test
    public void testGetTransactionsForAccount() throws EntityNotFoundException, JsonProcessingException {
        // Set up test data in the database using transactionRepository
        UUID accountId = UUID.randomUUID();
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

        // Call the method to be tested
        List<TransactionResponse> responses = transactionService.getTransactionsForAccount(account.accountId());

        // Assert the results
        assertNotNull(responses);
        assertEquals(2, responses.size());
        // Add more assertions as needed
    }

    // Additional integration tests as needed for error cases, edge cases, etc.
}
