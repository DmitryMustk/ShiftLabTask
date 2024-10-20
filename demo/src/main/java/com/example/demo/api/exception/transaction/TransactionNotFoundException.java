package com.example.demo.api.exception.transaction;

import com.example.demo.api.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends BaseException {
    public TransactionNotFoundException(Long id) {
        super("Transaction with ID: %d not found".formatted(id));
    }
}
