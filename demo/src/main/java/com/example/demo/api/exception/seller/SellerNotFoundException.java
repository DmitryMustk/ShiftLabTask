package com.example.demo.api.exception.seller;

import com.example.demo.api.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SellerNotFoundException extends BaseException {
    public SellerNotFoundException(Long id) {
        super("Seller with ID: %d not found".formatted(id));
    }
}
