package com.example.demo.api.controller;

import com.example.demo.api.dto.AckDto;
import com.example.demo.api.dto.SellerDto;
import com.example.demo.api.dto.TransactionDto;
import com.example.demo.api.factory.SellerDtoFactory;
import com.example.demo.api.factory.TransactionDtoFactory;
import com.example.demo.api.service.TransactionService;
import com.example.demo.store.entity.SellerEntity;
import com.example.demo.store.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class TransactionControllerTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionDtoFactory transactionDtoFactory;

    @Mock
    private SellerDtoFactory sellerDtoFactory;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionEntity transaction;
    private SellerEntity seller;
    private TransactionDto transactionDto;
    private SellerDto sellerDto;

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

        transactionDto = new TransactionDto(); // Инициализация полей для TransactionDto
        sellerDto = new SellerDto(); // Инициализация полей для SellerDto
    }

    @Test
    void fetchTransactions_shouldReturnListOfTransactionDtos() {
        when(transactionService.getAllTransactionsStream()).thenReturn(Stream.of(transaction));
        when(transactionDtoFactory.makeTransactionDto(transaction)).thenReturn(transactionDto);

        List<TransactionDto> result = transactionController.fetchTransactions();

        assertEquals(1, result.size());
        assertEquals(transactionDto, result.get(0));
        verify(transactionService).getAllTransactionsStream();
    }

    @Test
    void fetchTransactionById_shouldReturnTransactionDto_whenTransactionExists() {
        when(transactionService.getTransactionOrThrowException(1L)).thenReturn(transaction);
        when(transactionDtoFactory.makeTransactionDto(transaction)).thenReturn(transactionDto);

        TransactionDto result = transactionController.fetchTransactionById(1L);

        assertEquals(transactionDto, result);
        verify(transactionService).getTransactionOrThrowException(1L);
    }

    @Test
    void createTransaction_shouldReturnTransactionDto_whenCreatedSuccessfully() {
        when(transactionService.createTransaction(1L, BigDecimal.valueOf(100), "CREDIT_CARD")).thenReturn(transaction);
        when(transactionDtoFactory.makeTransactionDto(transaction)).thenReturn(transactionDto);

        TransactionDto result = transactionController.createTransaction(1L, BigDecimal.valueOf(100), "CREDIT_CARD");

        assertEquals(transactionDto, result);
        verify(transactionService).createTransaction(1L, BigDecimal.valueOf(100), "CREDIT_CARD");
    }

    @Test
    void updateTransaction_shouldReturnUpdatedTransactionDto_whenUpdatedSuccessfully() {
        when(transactionService.updateTransaction(1L, Optional.of(1L), Optional.of(BigDecimal.valueOf(150)), Optional.of("CREDIT_CARD"))).thenReturn(transaction);
        when(transactionDtoFactory.makeTransactionDto(transaction)).thenReturn(transactionDto);

        TransactionDto result = transactionController.updateTransaction(1L, Optional.of(1L), Optional.of(BigDecimal.valueOf(150)), Optional.of("CREDIT_CARD"));

        assertEquals(transactionDto, result);
        verify(transactionService).updateTransaction(1L, Optional.of(1L), Optional.of(BigDecimal.valueOf(150)), Optional.of("CREDIT_CARD"));
    }

//    @Test
//    void deleteTransaction_shouldReturnAckDto_whenDeletedSuccessfully() {
//        when(transactionService.deleteTransaction(1L)).thenReturn(AckDto.builder().answer(true).build());
//
//        AckDto result = transactionController.deleteTransaction(1L);
//
//        assertNotNull(result);
//        assertTrue(result.getAnswer());
//        verify(transactionService).deleteTransaction(1L);
//    }

    @Test
    void getMostProductiveSeller_shouldReturnSellerDto_whenSellerExists() {
        when(transactionService.getMostProductiveSeller(any(), any())).thenReturn(Optional.of(seller));
        when(sellerDtoFactory.makeSellerDto(seller)).thenReturn(sellerDto);

        SellerDto result = transactionController.getMostProductiveSeller("2023-10-01T00:00:00", "2023-10-31T23:59:59");

        assertEquals(sellerDto, result);
        verify(transactionService).getMostProductiveSeller(any(), any());
    }

    @Test
    void getSellersWithTotalTransactionLessThan_shouldReturnListOfSellerDtos() {
        when(transactionService.getSellersWithTotalTransactionLessThan(BigDecimal.valueOf(200))).thenReturn(List.of(seller));
        when(sellerDtoFactory.makeSellerDto(seller)).thenReturn(sellerDto);

        List<SellerDto> result = transactionController.getSellersWithTotalTransactionLessThan(BigDecimal.valueOf(200));

        assertEquals(1, result.size());
        assertEquals(sellerDto, result.get(0));
        verify(transactionService).getSellersWithTotalTransactionLessThan(BigDecimal.valueOf(200));
    }

    @Test
    void findBestTransactionPeriod_shouldReturnLocalDateTimeArray_whenPeriodExists() {
        LocalDateTime[] period = {LocalDateTime.now(), LocalDateTime.now().plusDays(1)};
        when(transactionService.findBestTransactionPeriod(1L)).thenReturn(Optional.of(period));

        LocalDateTime[] result = transactionController.findBestTransactionPeriod(1L);

        assertArrayEquals(period, result);
        verify(transactionService).findBestTransactionPeriod(1L);
    }
}
