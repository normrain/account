package com.example.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = -3332292346834265371L;

    public EntityNotFoundException(String cause, UUID id){
        super(String.format("%s with id %s not found", cause, id.toString()));
    }
}
