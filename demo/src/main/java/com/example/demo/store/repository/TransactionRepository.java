package com.example.demo.store.repository;

import com.example.demo.store.entity.SellerEntity;
import com.example.demo.store.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Stream<TransactionEntity> streamAllBy();
    List<TransactionEntity> findAllBySellerEntity(SellerEntity seller);
    Stream<TransactionEntity> findAllByTransactionDateBetween(LocalDateTime from, LocalDateTime to);
}
