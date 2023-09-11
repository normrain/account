package com.example.account.domain.balances.repository;

import com.example.account.domain.balances.entity.Balance;
import com.example.account.util.enums.Currency;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Mapper
@Repository
public interface BalanceRepository {

    @Select("SELECT * FROM balance WHERE account_id = #{id}")
    @Result(property = "accountId", column = "account_id")
    List<Balance> findByAccountId(UUID accountID);

    @Select("SELECT * FROM balance WHERE account_id = #{accountId} AND currency = #{currency}")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    @Result(property = "accountId", column = "account_id")
    Balance findByAccountIdAndCurrency(UUID accountId, Currency currency);

    @Insert("INSERT INTO balance(account_id, currency) VALUES (#{accountId}, #{currency}) RETURNING id")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insert(Balance balance);

    @Update("UPDATE balance SET balance=#{balance} WHERE id=#{id}")
    void updateBalance(UUID id, BigDecimal balance);
}
