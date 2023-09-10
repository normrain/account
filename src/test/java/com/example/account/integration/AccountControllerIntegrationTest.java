package com.example.account.integration;

import com.example.account.PostgresTestContainer;
import com.example.account.RabbitTestContainer;
import com.example.account.domain.accounts.api.model.AccountRequest;
import com.example.account.domain.accounts.api.model.AccountResponse;
import com.example.account.domain.accounts.service.AccountService;
import com.example.account.domain.transactions.model.TransactionRequest;
import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer<PostgresTestContainer> postgreSQLContainer = PostgresTestContainer.getInstance();

    @ClassRule
    public static RabbitTestContainer rabbitTestContainer = RabbitTestContainer.getInstance();

    @Autowired
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAccount() throws Exception {
        // Create a sample AccountRequest
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        AccountRequest accountRequest = new AccountRequest(customerId, "NL", Arrays.asList(Currency.USD, Currency.EUR));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").exists());
    }

    @Test
    public void testCreateTransaction() throws Exception {
        Long customerId = ThreadLocalRandom.current().nextLong(0, 1001);
        UUID accountId = accountService.createAccountAndBalances(
                new AccountRequest(customerId, "NL", Arrays.asList(Currency.USD, Currency.EUR))).accountId();

        TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(50.00), Currency.USD, Direction.IN, "Test Transaction");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/" + accountId + "/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void testGetAccount() throws Exception {
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountId.toString()));
    }

    @Test
    public void testGetTransactions() throws Exception {
        // Create a sample account and retrieve its ID
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
}
