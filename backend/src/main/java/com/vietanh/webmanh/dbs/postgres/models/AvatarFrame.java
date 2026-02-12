package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "avatar-frame")
public class AvatarFrame extends AppItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer avatarFrameId;

    @Column(nullable = false)
    String avatarFrameSrc;
}
