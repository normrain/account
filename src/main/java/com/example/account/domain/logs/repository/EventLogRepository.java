package com.example.account.domain.logs.repository;

import com.example.account.domain.logs.entity.EventLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EventLogRepository {

    @Insert("INSERT INTO event_log(event_type, object_id) VALUES (#{eventType}, #{objectId})")
    void insert(EventLog eventLog);

}
