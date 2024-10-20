package com.example.demo.api.exception.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionBadRequestException extends RuntimeException {
    public TransactionBadRequestException(String message) {
        super(message);
    }
}
