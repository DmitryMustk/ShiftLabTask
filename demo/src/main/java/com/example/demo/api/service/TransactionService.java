package com.example.demo.api.service;

import com.example.demo.api.exception.seller.SellerNotFoundException;
import com.example.demo.api.exception.transaction.TransactionBadRequestException;
import com.example.demo.api.exception.transaction.TransactionNotFoundException;
import com.example.demo.store.entity.SellerEntity;
import com.example.demo.store.entity.TransactionEntity;
import com.example.demo.store.repository.SellerRepository;
import com.example.demo.store.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public Stream<TransactionEntity> getAllTransactionsStream() {
        return transactionRepository.streamAllBy();
    }

    public TransactionEntity getTransactionOrThrowException(Long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new TransactionNotFoundException(id)
        );
    }

    public TransactionEntity createTransaction(Long sellerId, BigDecimal amount, String paymentType) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransactionBadRequestException("Amount cannot be negative");
        }

        SellerEntity seller = sellerRepository.findById(sellerId).orElseThrow(
                () -> new SellerNotFoundException(sellerId)
        );

        TransactionEntity.PaymentType validPaymentType;
        try {
            validPaymentType = TransactionEntity.PaymentType.valueOf(paymentType);
        } catch (IllegalArgumentException e) {
            throw new TransactionBadRequestException("Invalid payment type");
        }

        return transactionRepository.saveAndFlush(
                TransactionEntity.builder()
                        .sellerEntity(seller)
                        .amount(amount)
                        .paymentType(validPaymentType)
                        .transactionDate(LocalDateTime.now())
                        .build()
        );
    }

    public TransactionEntity updateTransaction(
            Long transactionId,
            Optional<Long> optionalSellerId,
            Optional<BigDecimal> optionalAmount,
            Optional<String> optionalPaymentType
    ) {
        TransactionEntity transaction = getTransactionOrThrowException(transactionId);

        optionalSellerId.flatMap(sellerRepository::findById).ifPresent(transaction::setSellerEntity);

        optionalAmount
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) >= 0)
                .ifPresent(transaction::setAmount);

        optionalPaymentType
                .filter(paymentType -> {
                    try {
                        TransactionEntity.PaymentType.valueOf(paymentType);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .ifPresent(paymentType ->
                        transaction.
                                setPaymentType(
                                        TransactionEntity.PaymentType.valueOf(paymentType)
                                )
                );

        return transactionRepository.saveAndFlush(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.delete(getTransactionOrThrowException(id));
    }

    public Optional<SellerEntity> getMostProductiveSeller(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return transactionRepository.findAllByTransactionDateBetween(startDateTime, endDateTime)
                .collect(Collectors.groupingBy(
                        TransactionEntity::getSellerEntity,
                        Collectors.reducing(BigDecimal.ZERO, TransactionEntity::getAmount, BigDecimal::add)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public List<SellerEntity> getSellersWithTotalTransactionLessThan(BigDecimal amount) {
        return sellerRepository.findAll().stream()
                .filter(seller -> transactionRepository.findAllBySellerEntity(seller)
                        .stream()
                        .map(TransactionEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .compareTo(amount) < 0)
                .collect(Collectors.toList());
    }

    public Optional<LocalDateTime[]> findBestTransactionPeriod(Long sellerId) {
        return sellerRepository.findById(sellerId)
                .flatMap(seller -> transactionRepository.findAllBySellerEntity(seller).stream()
                        .collect(Collectors.groupingBy(
                                transaction -> transaction.getTransactionDate().toLocalDate(),
                                Collectors.counting()))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(entry -> new LocalDateTime[]{entry.getKey().atStartOfDay(), entry.getKey().plusDays(1).atStartOfDay()})
                );
    }


}
