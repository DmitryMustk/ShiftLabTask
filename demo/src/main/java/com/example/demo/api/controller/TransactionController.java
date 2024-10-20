package com.example.demo.api.controller;

import com.example.demo.api.dto.AckDto;
import com.example.demo.api.dto.SellerDto;
import com.example.demo.api.dto.TransactionDto;
import com.example.demo.api.factory.SellerDtoFactory;
import com.example.demo.api.factory.TransactionDtoFactory;
import com.example.demo.api.service.TransactionService;
import com.example.demo.store.entity.SellerEntity;
import com.example.demo.store.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionDtoFactory transactionDtoFactory;
    private final SellerDtoFactory sellerDtoFactory;
    private final TransactionService transactionService;

    public static final String FETCH_TRANSACTIONS =      "/api/transactions";
    public static final String FETCH_TRANSACTION_BY_ID = "/api/transactions/{id}";
    public static final String CREATE_TRANSACTION =      "/api/transactions";
    public static final String UPDATE_TRANSACTION =      "/api/transactions/{id}";
    public static final String DELETE_TRANSACTION =      "/api/transactions/{id}";

    public static final String FETCH_MOST_PRODUCTIVE_SELLER       = "/api/transactions/most_productive";
    public static final String FETCH_SELLERS_WITH_TOTAL_LESS_THAN = "/api/transactions/sellers/less_than}";
    public static final String FIND_BEST_TRANSACTION_PERIOD       = "/api/transactions/best_period/{sellerId}";

    @GetMapping(FETCH_TRANSACTIONS)
    public List<TransactionDto> fetchTransactions() {
         return transactionService.getAllTransactionsStream()
                 .map(transactionDtoFactory::makeTransactionDto)
                 .toList();
    }

    @GetMapping(FETCH_TRANSACTION_BY_ID)
    public TransactionDto fetchTransactionById(
            @PathVariable("id") Long id
    ) {
        return transactionDtoFactory
                .makeTransactionDto(transactionService
                        .getTransactionOrThrowException(id));
    }

    @PostMapping(CREATE_TRANSACTION)
    public TransactionDto createTransaction(
            @RequestParam("seller_id") Long sellerId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("payment_type") String paymentType
    ) {
        return transactionDtoFactory.
                makeTransactionDto(transactionService
                        .createTransaction(sellerId, amount, paymentType));
    }

    @PatchMapping(UPDATE_TRANSACTION)
    public TransactionDto updateTransaction(
            @PathVariable(name = "id") Long transactionId,
            @RequestParam(name = "seller_id", required = false) Optional<Long> optionalSellerId,
            @RequestParam(name = "amount", required = false) Optional<BigDecimal> optionalAmount,
            @RequestParam(name = "payment_type", required = false) Optional<String> optionalPaymentType
    ) {
        return transactionDtoFactory
                .makeTransactionDto(transactionService
                        .updateTransaction(transactionId, optionalSellerId, optionalAmount, optionalPaymentType));
    }

    @DeleteMapping(DELETE_TRANSACTION)
    public AckDto deleteTransaction(
            @PathVariable(name = "id") Long transactionId
    ) {
        transactionService.deleteTransaction(transactionId);

        return AckDto.builder()
                .answer(true)
                .build();
    }


    @GetMapping(FETCH_MOST_PRODUCTIVE_SELLER)
    public SellerDto getMostProductiveSeller(
            @RequestParam("startDateTime") String startDateTimeStr,
            @RequestParam("endDateTime") String endDateTimeStr) {
        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr);
        return transactionService.getMostProductiveSeller(startDateTime, endDateTime)
                .map(sellerDtoFactory::makeSellerDto)
                .orElse(null);
    }

    @GetMapping(FETCH_SELLERS_WITH_TOTAL_LESS_THAN)
    public List<SellerDto> getSellersWithTotalTransactionLessThan(@RequestParam BigDecimal amount) {
        return transactionService.getSellersWithTotalTransactionLessThan(amount)
                .stream()
                .map(sellerDtoFactory::makeSellerDto)
                .toList();
    }

    @GetMapping(FIND_BEST_TRANSACTION_PERIOD)
    public LocalDateTime[] findBestTransactionPeriod(@PathVariable Long sellerId) {
        return transactionService.findBestTransactionPeriod(sellerId)
                .orElse(null); // Обработка отсутствия периода
    }
}
