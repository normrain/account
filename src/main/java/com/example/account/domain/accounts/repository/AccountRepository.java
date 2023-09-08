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
    @Results(id = "companyResultMap", value = {
            @Result(property = "country", column="country"),
            @Result(property = "customerId", column = "customer_id")
    })
    Account findById(UUID id);

    @Insert("INSERT INTO account(country, customer_id) " +
            " VALUES (#{country}, #{customerId})" +
            "RETURNING id")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insert(Account account);

    UUID update(Account account);
}
