package com.example.demo.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contactInfo;

    @Column(nullable = false)
    private LocalDateTime registrationDate;
}
