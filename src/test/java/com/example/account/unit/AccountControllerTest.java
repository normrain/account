package com.example.account.unit;

import com.example.account.domain.accounts.api.controller.AccountController;
import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.domain.transactions.model.TransactionResponse;
import com.example.account.domain.transactions.service.TransactionService;
import com.example.account.util.exception.EntityNotFoundException;
import com.example.account.util.exception.InvalidBalanceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        // Arrange
        AccountRequest accountRequest = new AccountRequest("customerId", "country", new ArrayList<>());
        AccountResponse expectedResponse = new AccountResponse(UUID.randomUUID(), "customerId", new ArrayList<>());

        when(accountService.createAccountAndBalances(accountRequest)).thenReturn(expectedResponse);

        // Act
        AccountResponse result = accountController.createAccount(accountRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testCreateTransaction() throws InvalidBalanceException, EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(
                null, // Provide appropriate transaction data
                null,
                null,
                null
        );
        TransactionResponse expectedResponse = new TransactionResponse();

        when(transactionService.createTransaction(accountId, transactionRequest)).thenReturn(expectedResponse);

        // Act
        TransactionResponse result = accountController.createTransaction(accountId, transactionRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testGetAccount() throws EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        AccountResponse expectedResponse = new AccountResponse(accountId, "customerId", new ArrayList<>());

        when(accountService.getAccountWithBalances(accountId)).thenReturn(expectedResponse);

        // Act
        AccountResponse result = accountController.getAccount(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testGetTransactions() throws EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        List<TransactionResponse> expectedResponses = new ArrayList<>();

        when(transactionService.getTransactionsForAccount(accountId)).thenReturn(expectedResponses);

        // Act
        List<TransactionResponse> result = accountController.getTransactions(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponses, result);
    }

    @Test(expected = MethodArgumentNotValidException.class)
    public void testCreateAccountValidationFailure() throws JsonProcessingException {
        // Arrange
        AccountRequest accountRequest = new AccountRequest("", "", new ArrayList<>()); // Invalid data
        when(accountService.createAccountAndBalances(accountRequest)).thenThrow(MethodArgumentNotValidException.class);

        // Act and Assert
        accountController.createAccount(accountRequest);
    }

    @Test(expected = MethodArgumentNotValidException.class)
    public void testCreateTransactionValidationFailure() throws InvalidBalanceException, EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest(
                null, // Invalid data
                null,
                null,
                null
        );
        when(transactionService.createTransaction(accountId, transactionRequest)).thenThrow(MethodArgumentNotValidException.class);

        // Act and Assert
        accountController.createTransaction(accountId, transactionRequest);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetAccountEntityNotFound() throws EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        when(accountService.getAccountWithBalances(accountId)).thenThrow(EntityNotFoundException.class);

        // Act and Assert
        accountController.getAccount(accountId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetTransactionsEntityNotFound() throws EntityNotFoundException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        when(transactionService.getTransactionsForAccount(accountId)).thenThrow(EntityNotFoundException.class);

        // Act and Assert
        accountController.getTransactions(accountId);
    }
}
