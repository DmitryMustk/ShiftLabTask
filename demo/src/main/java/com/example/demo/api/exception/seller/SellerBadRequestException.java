package com.example.demo.api.exception.seller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SellerBadRequestException extends RuntimeException {
    public SellerBadRequestException(String message) {
        super(message);
    }
}
