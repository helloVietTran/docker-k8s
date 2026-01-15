package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "coin_transaction")
public class CoinTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer transactionId;

    @Column(nullable = false)
    Integer walletId;

    @Column(nullable = false)
    Integer amount;

    @Column(nullable = false)
    Integer balanceBefore;

    @Column(nullable = false)
    Integer balanceAfter;

    @Column(nullable = false)
    String transactionType;

    String referenceType;
    Integer referenceId;

    String description;

    @Column(nullable = false)
    Instant createdAt;
}

