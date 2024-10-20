package com.example.demo.api.service;

import com.example.demo.api.exception.seller.SellerNotFoundException;
import com.example.demo.api.exception.transaction.TransactionBadRequestException;
import com.example.demo.api.exception.transaction.TransactionNotFoundException;
import com.example.demo.store.entity.SellerEntity;
import com.example.demo.store.entity.TransactionEntity;
import com.example.demo.store.repository.SellerRepository;
import com.example.demo.store.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private SellerEntity seller;
    private TransactionEntity transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seller = new SellerEntity();
        seller.setId(1L);
        seller.setName("Seller A");

        transaction = TransactionEntity.builder()
                .id(1L)
                .sellerEntity(seller)
                .amount(BigDecimal.valueOf(100))
                .paymentType(TransactionEntity.PaymentType.CARD)
                .transactionDate(LocalDateTime.now())
                .build();
    }

    @Test
    void createTransaction_shouldCreateTransaction_whenValidInput() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.saveAndFlush(any(TransactionEntity.class))).thenReturn(transaction);

        TransactionEntity result = transactionService.createTransaction(1L, BigDecimal.valueOf(100), "CARD");

        assertNotNull(result);
        assertEquals(transaction.getId(), result.getId());
        assertEquals(transaction.getAmount(), result.getAmount());
        verify(transactionRepository).saveAndFlush(any(TransactionEntity.class));
    }

    @Test
    void createTransaction_shouldThrowSellerNotFoundException_whenSellerDoesNotExist() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SellerNotFoundException.class, () ->
                transactionService.createTransaction(1L, BigDecimal.valueOf(100), "CARD"));

        assertEquals("Seller with ID: 1 not found", exception.getMessage());
    }

    @Test
    void createTransaction_shouldThrowTransactionBadRequestException_whenAmountIsNegative() {
        Exception exception = assertThrows(TransactionBadRequestException.class, () ->
                transactionService.createTransaction(1L, BigDecimal.valueOf(-100), "CARD"));

        assertEquals("Amount cannot be negative", exception.getMessage());
    }

    @Test
    void updateTransaction_shouldUpdateTransaction_whenValidInput() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.saveAndFlush(transaction)).thenReturn(transaction);

        TransactionEntity updatedTransaction = transactionService.updateTransaction(1L, Optional.of(1L), Optional.of(BigDecimal.valueOf(150)), Optional.of("CREDIT_CARD"));

        assertEquals(BigDecimal.valueOf(150), updatedTransaction.getAmount());
        verify(transactionRepository).saveAndFlush(transaction);
    }

    @Test
    void updateTransaction_shouldThrowTransactionNotFoundException_whenTransactionDoesNotExist() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionNotFoundException.class, () ->
                transactionService.updateTransaction(1L, Optional.empty(), Optional.empty(), Optional.empty()));

        assertEquals("Transaction with ID: 1 not found", exception.getMessage());
    }

    @Test
    void deleteTransaction_shouldDeleteTransaction_whenTransactionExists() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransaction_shouldThrowTransactionNotFoundException_whenTransactionDoesNotExist() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransaction(1L));

        assertEquals("Transaction with ID: 1 not found", exception.getMessage());
    }

    @Test
    void getMostProductiveSeller_shouldReturnMostProductiveSeller_whenTransactionsExist() {
        TransactionEntity transaction2 = TransactionEntity.builder()
                .sellerEntity(seller)
                .amount(BigDecimal.valueOf(200))
                .paymentType(TransactionEntity.PaymentType.CARD)
                .transactionDate(LocalDateTime.now())
                .build();

        when(transactionRepository.findAllByTransactionDateBetween(any(), any())).thenReturn(Stream.of(transaction, transaction2));

        Optional<SellerEntity> result = transactionService.getMostProductiveSeller(LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertTrue(result.isPresent());
        assertEquals(seller.getId(), result.get().getId());
    }
}
