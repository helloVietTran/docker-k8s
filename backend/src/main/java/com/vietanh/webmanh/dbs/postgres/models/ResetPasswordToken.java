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
@Table(name = "reset_password_token")
public class ResetPasswordToken  {
    @Id
    String hashedToken;

    @Column(nullable = false)
    Integer userId;

    @Builder.Default
    boolean isUsed = false;

    Instant createdAt;
    Instant expiryAt;
}