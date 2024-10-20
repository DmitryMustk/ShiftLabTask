package com.example.demo.api.factory;

import com.example.demo.api.dto.TransactionDto;
import com.example.demo.store.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoFactory {
    private SellerDtoFactory sellerDtoFactory;

    public TransactionDto makeTransactionDto(TransactionEntity transactionEntity) {
        return TransactionDto.builder()
                .id(transactionEntity.getId())
                .seller(sellerDtoFactory.makeSellerDto(transactionEntity.getSellerEntity()))
                .amount(transactionEntity.getAmount())
                .paymentType(transactionEntity.getPaymentType().toString())
                .transactionDate(transactionEntity.getTransactionDate())
                .build();
    }
}
