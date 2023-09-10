package com.example.account.domain.transactions.entity;

import com.example.account.util.enums.Currency;
import com.example.account.util.enums.Direction;
import lombok.*;

import java.math.BigDecimal;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Transaction {
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private Currency currency;
    private Direction direction;
    private String description;
}
