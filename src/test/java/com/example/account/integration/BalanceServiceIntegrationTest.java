package com.example.account.integration;

import com.example.account.utils.PostgresTestContainer;
import com.example.account.utils.RabbitTestContainer;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
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
    public void withValidBalances_returnBalances() {
        UUID accountId = UUID.randomUUID();

        Balance balance1 = new Balance(UUID.randomUUID(), accountId, BigDecimal.ZERO, Currency.USD);
        Balance balance2 = new Balance(UUID.randomUUID(), accountId, BigDecimal.ZERO, Currency.EUR);

        balanceRepository.insert(balance1);
        balanceRepository.insert(balance2);

        List<BalanceResponse> balances = balanceService.getBalancesForAccount(accountId);

        assertNotNull(balances);
        assertEquals(2, balances.size());

        assertEquals(BigDecimal.ZERO, balances.get(0).balance());
        assertEquals(Currency.USD, balances.get(0).currency());

        assertEquals(BigDecimal.ZERO, balances.get(1).balance());
        assertEquals(Currency.EUR, balances.get(1).currency());
    }

    @Test
    public void withValidBalances_createBalances() {
        UUID accountId = UUID.randomUUID();
        List<Currency> currencies = List.of(Currency.USD, Currency.EUR);

        balanceService.createBalancesForAccount(accountId, currencies);

        List<Balance> balances = balanceRepository.findByAccountId(accountId);

        assertNotNull(balances);
        assertEquals(2, balances.size());

        assertNotNull(balances.get(0).getId());
        assertEquals(accountId, balances.get(0).getAccountId());
        assertEquals(BigDecimal.ZERO, balances.get(0).getBalance());
        assertEquals(Currency.USD, balances.get(0).getCurrency());

        assertNotNull(balances.get(1).getId());
        assertEquals(accountId, balances.get(1).getAccountId());
        assertEquals(BigDecimal.ZERO, balances.get(1).getBalance());
        assertEquals(Currency.EUR, balances.get(1).getCurrency());
    }

    @Test
    public void withValidBalanceAndValidTransactionDirection_updateBalance() throws InvalidBalanceException {
        UUID accountId = UUID.randomUUID();
        Balance balance = new Balance(null, accountId, null, Currency.USD);
        balanceRepository.insert(balance);
        BigDecimal newBalanceAmount = balanceService.updateAccountBalance(accountId, BigDecimal.valueOf(50.00), Direction.IN, Currency.USD);

        assertNotNull(newBalanceAmount);
        assertEquals(BigDecimal.valueOf(50.00).setScale(2, RoundingMode.HALF_UP), newBalanceAmount);
    }

    @Test(expected = InvalidBalanceException.class)
    public void withValidBalanceAndInsufficientFunds_throwException() throws InvalidBalanceException {
        UUID accountId = UUID.randomUUID();
        Balance balance = new Balance(null, accountId, null, Currency.USD);
        balanceRepository.insert(balance);

        BigDecimal newBalanceAmount = balanceService.updateAccountBalance(accountId, BigDecimal.valueOf(50.00), Direction.OUT, Currency.USD);

        assertNotNull(newBalanceAmount);
        assertEquals(BigDecimal.valueOf(50.00).setScale(2, RoundingMode.HALF_UP), newBalanceAmount);
    }

}
