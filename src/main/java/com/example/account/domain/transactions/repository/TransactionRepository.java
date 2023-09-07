package com.example.account.domain.transactions.repository;

import com.example.account.domain.transactions.entity.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
    Transaction findById(UUID id);

    @Insert("INSERT INTO transaction(country, customer_id, currency) " +
            " VALUES (#{country}, #{customer_id}, #{currency})")
    void insert(Transaction transaction);

    @Update("Update transaction set title=#{title}, " +
            " isbn=#{isbn}, description=#{description}, page=#{page}, price=#{price} where id=#{id}")
    UUID update(Transaction transaction);
}
