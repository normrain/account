package com.example.account.domain.balances.entity;

import com.example.account.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Balance {
    private UUID id;
    private UUID accountId;
    private BigDecimal balance;
    private Currency currency;
}
