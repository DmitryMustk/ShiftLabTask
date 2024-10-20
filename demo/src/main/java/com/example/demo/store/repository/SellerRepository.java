package com.example.demo.store.repository;

import com.example.demo.store.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface SellerRepository extends JpaRepository<SellerEntity, Long> {
    Stream<SellerEntity> streamAllBy();
    Optional<SellerEntity> findByName(String name);

}
