package com.example.account.domain.accounts.entity;

import com.example.account.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private UUID id;
    private String country;
    private UUID customerId;
    private List<Currency> currencies;
}
