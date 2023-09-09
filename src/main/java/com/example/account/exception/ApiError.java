package com.example.account.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Setter
public class ApiError {

    private HttpStatus status;
    private Instant timestamp;
    private String message;

    ApiError(HttpStatus status, String message) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
    }
}
