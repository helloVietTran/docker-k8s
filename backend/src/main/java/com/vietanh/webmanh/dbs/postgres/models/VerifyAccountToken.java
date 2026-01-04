package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "verify_account_token")
public class VerifyAccountToken {
    @Id
    String verifyToken;

    @Column(nullable = false)
    Integer userId;

    Instant createdAt;
    Instant expiryAt;

    public boolean isValidToken(){
        return this.expiryAt.isAfter(Instant.now());
    }
}
