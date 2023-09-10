package com.example.account.integration;

import com.example.account.PostgresTestContainer;
import com.example.account.RabbitTestContainer;
import com.example.account.domain.balances.entity.Balance;
import com.example.account.domain.balances.model.BalanceResponse;
import com.example.account.domain.balances.repository.BalanceRepository;
import com.example.account.domain.balances.service.BalanceService;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import com.example.account.util.exception.InvalidBalanceException;
import com.example.account.service.RabbitMqSenderService;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BalanceServiceIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer<PostgresTestContainer> postgreSQLContainer = PostgresTestContainer.getInstance();

    @ClassRule
    public static RabbitTestContainer rabbitTestContainer = RabbitTestContainer.getInstance();

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
        Balance balance1 = new Balance(UUID.randomUUID(), accountId, BigDecimal.valueOf(100.00), Currency.USD);
        Balance balance2 = new Balance(UUID.randomUUID(), accountId,BigDecimal.valueOf(50.00), Currency.EUR);
        balanceRepository.insert(balance1);
        balanceRepository.insert(balance2);

        //balanceRepository.saveAll(Arrays.asList(balance1, balance2));

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
        UUID accountId = UUID.randomUUID();
        Balance balance = new Balance(null, accountId, null, Currency.USD);
        balanceRepository.insert(balance);
        System.out.println(balanceRepository.findById(balance.getId()));
        BigDecimal newBalanceAmount = balanceService.updateAccountBalance(accountId, BigDecimal.valueOf(50.00), Direction.IN, Currency.USD);

        assertNotNull(newBalanceAmount);
        assertEquals(BigDecimal.valueOf(50.00).setScale(2, RoundingMode.HALF_UP), newBalanceAmount);
    }

    // Additional integration tests as needed for error cases, edge cases, etc.
}
