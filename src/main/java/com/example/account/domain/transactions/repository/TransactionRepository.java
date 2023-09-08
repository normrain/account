package com.example.account.domain.transactions.repository;

import com.example.account.domain.transactions.entity.Transaction;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Mapper
@Repository
public interface TransactionRepository {

    String SELECT_FROM_TRANSACTION_WHERE_ID = "SELECT * FROM transaction WHERE id = #{id}";

    @Select("select * from transaction")
    List<Transaction> findAll();

    @Select(SELECT_FROM_TRANSACTION_WHERE_ID)
    @Result(property = "customerId", column = "customer_id")
    Transaction findById(UUID id);

    @Select("SELECT * FROM transaction WHERE account_id = #{accountId}")
    @Result(property = "accountId", column = "account_id")
    List<Transaction> findByAccountId(UUID accountId);

    @Insert("INSERT INTO transaction(account_id, amount, currency, direction, description) " +
            " VALUES (#{accountId}, #{amount}, #{currency}, #{direction}, #{description})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insert(Transaction transaction);

}
