package com.example.account.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Select;
import org.springframework.http.HttpStatus;
import org.springframework.web.service.annotation.GetExchange;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    private HttpStatus status;
    private Instant timestamp;
    private String message;

    ApiError(HttpStatus status, String message) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        this.status = status;
        this.message = message;
    }
}
