package com.example.account.domain.balances.entity;

import com.example.account.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Balance {
    private UUID id;
    private UUID accountId;
    private BigDecimal balance;
    private Currency currency;
}
