package com.example.account.integration;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void testCreateTransaction() throws InvalidBalanceException, EntityNotFoundException {
        // Set up test data
        UUID accountId = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(accountId, BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Test Transaction");

        // Call the method to be tested
        TransactionResponse response = transactionService.createTransaction(accountId, transactionRequest);

        // Retrieve the transaction from the database using transactionRepository
        Transaction transaction = transactionRepository.findById(response.getId());

        // Assert the results
        assertNotNull(response);
        assertNotNull(transaction);
        // Add more assertions as needed

        // Verify interactions with balanceService, accountService, and rabbitMqSenderService using Mockito
        verify(balanceService, times(1)).updateAccountBalance(any(UUID.class), any(BigDecimal.class), any(Direction.class), any(Currency.class));
        verify(accountService, times(1)).getAccountWithBalances(accountId);
        verify(rabbitMqSenderService, times(1)).sendMessageToQueue(any(UUID.class), eq(EventType.CREATION));
    }

    @Test
    public void testGetTransactionsForAccount() throws EntityNotFoundException {
        // Set up test data in the database using transactionRepository
        UUID accountId = UUID.randomUUID();
        Transaction transaction1 = new Transaction(accountId, BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Transaction 1");
        Transaction transaction2 = new Transaction(accountId, BigDecimal.valueOf(30.00), Currency.EUR, Direction.OUT, "Transaction 2");
        transactionRepository.saveAll(Arrays.asList(transaction1, transaction2));

        // Call the method to be tested
        List<TransactionResponse> responses = transactionService.getTransactionsForAccount(accountId);

        // Assert the results
        assertNotNull(responses);
        assertEquals(2, responses.size());
        // Add more assertions as needed
    }

    // Additional integration tests as needed for error cases, edge cases, etc.
}
