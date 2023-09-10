package com.example.account.util.exception;

import com.example.account.util.enums.Currency;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.UUID;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidBalanceException extends Exception {

    @Serial
    private static final long serialVersionUID = -3332292346834265371L;

    public InvalidBalanceException(String message, UUID accountId, Currency currency, UUID balanceId){
        super(String.format(
                "For Account: %s with Currency: %s (Balance: %S): %s",
                accountId.toString(),
                currency,
                balanceId,
                message));
    }

}
