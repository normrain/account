package com.example.account.integration;

import com.example.account.domain.transactions.service.TransactionService;
import com.example.account.utils.PostgresTestContainer;
import com.example.account.utils.RabbitTestContainer;
import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountControllerIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer<PostgresTestContainer> postgreSQLContainer = PostgresTestContainer.getInstance();

    @ClassRule
    public static RabbitTestContainer rabbitTestContainer = RabbitTestContainer.getInstance();

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void withValidAccountRequest_createAccountAndBalances() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "NL", Arrays.asList(Currency.USD, Currency.EUR));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId", equalTo(customerId.intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[0].balance", equalTo(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[0].currency", equalTo("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[1].balance", equalTo(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[1].currency", equalTo("EUR")));

    }

    @Test
    public void withInvalidAccountRequest_ThrowException() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "country", Arrays.asList(Currency.USD, Currency.EUR));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void withValidTransactionRequestAnExistingAccount_createTransaction() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        UUID accountId = accountService.createAccountAndBalances(
                new AccountRequest(customerId, "NL", Arrays.asList(Currency.USD, Currency.EUR))).accountId();

        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Test Transaction");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/" + accountId + "/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId", equalTo(accountId.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", equalTo(50.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", equalTo("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.direction", equalTo("IN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", equalTo("Test Transaction")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.newBalance", equalTo(50.00)));
    }
    @Test
    public void withInvalidTransactionRequest_throwException() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        UUID accountId = accountService.createAccountAndBalances(
                new AccountRequest(customerId, "NL", Arrays.asList(Currency.USD, Currency.EUR))).accountId();

        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(-5.00), Currency.USD, Direction.IN, "Test Transaction");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/" + accountId + "/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void withExistingAccount_getAccount() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "EE", Arrays.asList(Currency.USD, Currency.EUR));
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(response).get("accountId").asText());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId", equalTo(customerId.intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[0].balance", equalTo(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[0].currency", equalTo("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[1].balance", equalTo(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balances[1].currency", equalTo("EUR")));

    }

    @Test
    public void withNotExistingAccount_returnNotFoundException() throws Exception {
        UUID accountId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void withValidAccountAndNoTransactions_returnEmptyTransactions() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "EE", Arrays.asList(Currency.USD, Currency.EUR));
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(response).get("accountId").asText());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/" + accountId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void withValidAccountAndTransactions_returnTransactionList() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "EE", Arrays.asList(Currency.USD, Currency.EUR));
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(response).get("accountId").asText());
        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(5.00), Currency.USD, Direction.IN, "Test Transaction");

        transactionService.createTransaction(accountId, transactionRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/" + accountId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountId", equalTo(accountId.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount", equalTo(5.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].currency", equalTo("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].direction", equalTo("IN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", equalTo("Test Transaction")));
    }
}
