package com.example.account.domain.accounts.repository;

import com.example.account.domain.accounts.entity.Account;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
@Repository
public interface AccountRepository {

    @Select("SELECT * FROM account WHERE id = #{id}")
    @Result(property = "customerId", column = "customer_id")
    Account findById(UUID id);

    @Insert("INSERT INTO account(country, customer_id) " +
            " VALUES (#{country}, #{customerId})" +
            "RETURNING id")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insert(Account account);

}
