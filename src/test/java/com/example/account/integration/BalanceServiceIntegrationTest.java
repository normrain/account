package com.example.account.integration;

import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.repository.BalanceRepository;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BalanceServiceIntegrationTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private RabbitMqSenderService rabbitMqSenderService;

    @Test
    public void testGetBalancesForAccount() {
        // Set up test data in the database using balanceRepository
        UUID accountId = UUID.randomUUID();
        Balance balance1 = new Balance(accountId, Currency.USD, BigDecimal.valueOf(100.00));
        Balance balance2 = new Balance(accountId, Currency.EUR, BigDecimal.valueOf(50.00));
        balanceRepository.saveAll(Arrays.asList(balance1, balance2));

        // Call the method to be tested
        List<BalanceResponse> balances = balanceService.getBalancesForAccount(accountId);

        // Assert the results
        assertNotNull(balances);
        assertEquals(2, balances.size());
        // Add more assertions as needed
    }

    @Test
    public void testCreateBalancesForAccount() {
        // Set up test data
        UUID accountId = UUID.randomUUID();
        List<Currency> currencies = Arrays.asList(Currency.USD, Currency.EUR);

        // Call the method to be tested
        balanceService.createBalancesForAccount(accountId, currencies);

        // Retrieve the balances from the database using balanceRepository
        List<Balance> balances = balanceRepository.findByAccountId(accountId);

        // Assert the results
        assertNotNull(balances);
        assertEquals(2, balances.size());
        // Add more assertions as needed
    }

    @Test
    public void testUpdateAccountBalance() throws InvalidBalanceException {
        // Set up test data in the database using balanceRepository
        UUID accountId = UUID.randomUUID();
        Balance balance = new Balance(accountId, Currency.USD, BigDecimal.valueOf(100.00));
        balanceRepository.save(balance);

        // Call the method to be tested
        BigDecimal newBalanceAmount = balanceService.updateAccountBalance(accountId, BigDecimal.valueOf(50.00), Direction.IN, Currency.USD);

        // Assert the results
        assertNotNull(newBalanceAmount);
        assertEquals(BigDecimal.valueOf(150.00), newBalanceAmount);
        // Add more assertions as needed
    }

    // Additional integration tests as needed for error cases, edge cases, etc.
}
