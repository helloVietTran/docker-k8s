package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TextAnimationItem extends AppItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer textAnimationItemId;

    @Column(nullable = false, unique = true)
    String animationKey;
    // ví dụ: "RAINBOW_TEXT", "SHAKE", "NEON_GLOW"
}
