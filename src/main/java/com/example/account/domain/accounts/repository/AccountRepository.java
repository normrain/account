package com.example.account.domain.accounts.repository;

import com.example.account.domain.accounts.entity.Account;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Mapper
@Repository
public interface AccountRepository {

    String SELECT_FROM_ACCOUNT_WHERE_ID = "SELECT * FROM account WHERE id = #{id}";

    @Select("select * from account")
    List<Account> findAll();

    @Select(SELECT_FROM_ACCOUNT_WHERE_ID)
    Account findById(UUID id);

    @Insert("INSERT INTO account(country, customer_id, currency) " +
            " VALUES (#{country}, #{customer_id}, #{currency})")
    void insert(Account account);

    @Update("Update book set title=#{title}, " +
            " isbn=#{isbn}, description=#{description}, page=#{page}, price=#{price} where id=#{id}")
    UUID update(Account account);
}
