package com.example.account.unit;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private AccountService accountService;

    @Mock
    private RabbitMqSenderService rabbitMqSenderService;

    @Test
    public void testCreateTransaction() throws InvalidBalanceException, EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(
                BigDecimal.valueOf(50.00),
                Currency.USD,
                Direction.IN,
                "Description"
        );
        BigDecimal newBalance = BigDecimal.valueOf(150.00);

        when(accountService.getAccountWithBalances(accountId)).thenReturn(
                new AccountResponse(accountId, "customerId", new ArrayList<>())
        );
        when(balanceService.updateAccountBalance(accountId, transactionRequest.amount(), transactionRequest.direction(), transactionRequest.currency())).thenReturn(newBalance);

        // Act
        TransactionResponse result = transactionService.createTransaction(accountId, transactionRequest);

        // Assert
        assertNotNull(result);
        assertEquals(transactionRequest.amount(), result.amount());
        assertEquals(transactionRequest.currency(), result.currency());
        assertEquals(transactionRequest.direction(), result.direction());
        assertEquals(newBalance, result.newBalance());
    }

    @Test
    public void testGetTransactionsForAccount() throws EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(accountId, BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Description1"));
        transactions.add(new Transaction(accountId, BigDecimal.valueOf(25.00), Currency.EUR, Direction.OUT, "Description2"));

        when(accountService.getAccountWithBalances(accountId)).thenReturn(
                new AccountResponse(accountId, "customerId", new ArrayList<>())
        );
        when(transactionRepository.findByAccountId(accountId)).thenReturn(transactions);

        // Act
        List<TransactionResponse> result = transactionService.getTransactionsForAccount(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(transactions.size(), result.size());
        assertEquals(transactions.get(0).getAmount(), result.get(0).amount());
        assertEquals(transactions.get(0).getCurrency(), result.get(0).currency());
        assertEquals(transactions.get(0).getDirection(), result.get(0).direction());
        assertEquals(transactions.get(0).getDescription(), result.get(0).description());
        assertNull(result.get(0).newBalance()); // New balance is not calculated for historical transactions
    }
}