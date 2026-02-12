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
@Table(
        name = "item_purchase"
//        indexes = {
//                @Index(name = "idx_item_purchase_user", columnList = "userId"),
//                @Index(name = "idx_item_purchase_item", columnList = "itemType,itemId"),
//                @Index(name = "idx_item_purchase_expired", columnList = "expiredAt")
//        }
)
public class ItemPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Integer userId;

    @Column(nullable = false)
    String itemType; // AVATAR_FRAME, BADGE, THEME...

    @Column(nullable = false)
    Integer itemId;

    @Column(nullable = false)
    Instant purchasedAt;

    @Column(nullable = false)
    Instant expiredAt;

    @Column(nullable = false)
    Integer price;

    @Column(nullable = false)
    Instant createdAt;
}
