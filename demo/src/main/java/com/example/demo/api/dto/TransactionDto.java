package com.example.demo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    @NonNull
    private Long id;

    @NonNull
    private SellerDto seller;

    @NonNull
    private BigDecimal amount;

    @NonNull
    @JsonProperty("payment_type")
    private String paymentType;

    @NonNull
    @JsonProperty("transaction_date")
    private LocalDateTime transactionDate;
}

