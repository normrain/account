package com.example.account.domain.transactions.entity;

import com.example.account.entity.Direction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.util.Currency;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private UUID uuid;
    private UUID accountId;
    private BigDecimal amount;
    private Currency currency;
    private Direction direction;
    private String description;

}
