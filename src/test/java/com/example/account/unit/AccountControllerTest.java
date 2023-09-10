package com.example.account.unit;

import com.example.account.domain.accounts.api.controller.AccountController;
import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.domain.transactions.model.TransactionResponse;
import com.example.account.domain.transactions.service.TransactionService;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import com.example.account.util.exception.EntityNotFoundException;
import com.example.account.util.exception.InvalidBalanceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Test
    public void testCreateAccount() throws JsonProcessingException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "NL", new ArrayList<>());
        AccountResponse expectedResponse = new AccountResponse(UUID.randomUUID(), customerId, new ArrayList<>());

        when(accountService.createAccountAndBalances(accountRequest)).thenReturn(expectedResponse);

        AccountResponse result = accountController.createAccount(accountRequest);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testCreateTransaction() throws InvalidBalanceException, EntityNotFoundException {
        UUID accountId = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(
                BigDecimal.TEN, // Provide appropriate transaction data
                Currency.EUR,
                Direction.IN,
                "test description"
        );
        TransactionResponse expectedResponse = new TransactionResponse(
                accountId,
                UUID.randomUUID(),
                BigDecimal.TEN,
                Currency.EUR,
                Direction.IN,
                "test description",
                BigDecimal.TEN
        );

        when(transactionService.createTransaction(accountId, transactionRequest)).thenReturn(expectedResponse);

        TransactionResponse result = accountController.createTransaction(accountId, transactionRequest);

System.out.println(result);
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testGetAccount() throws EntityNotFoundException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        UUID accountId = UUID.randomUUID();
        AccountResponse expectedResponse = new AccountResponse(accountId, customerId, List.of());

        when(accountService.getAccountWithBalances(accountId)).thenReturn(expectedResponse);

        AccountResponse result = accountController.getAccount(accountId);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testGetTransactions() throws EntityNotFoundException {
        UUID accountId = UUID.randomUUID();
        List<TransactionResponse> expectedResponses = List.of(
                new TransactionResponse(accountId, UUID.randomUUID(), BigDecimal.ONE, Currency.EUR, Direction.IN, "test description 1", null),
                new TransactionResponse(accountId, UUID.randomUUID(), BigDecimal.TEN, Currency.USD, Direction.OUT, "test description 2", null)
        );

        when(transactionService.getTransactionsForAccount(accountId)).thenReturn(expectedResponses);

        List<TransactionResponse> result = accountController.getTransactions(accountId);

        assertNotNull(result);
        assertEquals(expectedResponses, result);
    }

    @Test(expected = MethodArgumentNotValidException.class)
    public void testCreateAccountValidationFailure() throws JsonProcessingException {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "", List.of());

        when(accountService.createAccountAndBalances(accountRequest)).thenThrow(MethodArgumentNotValidException.class);

        accountController.createAccount(accountRequest);
    }

    @Test(expected = MethodArgumentNotValidException.class)
    public void testCreateTransactionValidationFailure() throws InvalidBalanceException, EntityNotFoundException {
        UUID accountId = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(
                null,
                null,
                null,
                null
        );

        when(transactionService.createTransaction(accountId, transactionRequest)).thenThrow(MethodArgumentNotValidException.class);

        accountController.createTransaction(accountId, transactionRequest);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetAccountEntityNotFound() throws EntityNotFoundException {
        UUID accountId = UUID.randomUUID();
        when(accountService.getAccountWithBalances(accountId)).thenThrow(EntityNotFoundException.class);

        accountController.getAccount(accountId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetTransactionsEntityNotFound() throws EntityNotFoundException {
        UUID accountId = UUID.randomUUID();
        when(transactionService.getTransactionsForAccount(accountId)).thenThrow(EntityNotFoundException.class);

        accountController.getTransactions(accountId);
    }
}
