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
@Table(name = "coin_wallet")
public class CoinWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer walletId;

    @Column(nullable = false, unique = true)
    Integer userId;

    @Column(nullable = false)
    Integer balance;

    @Column(nullable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;
}
