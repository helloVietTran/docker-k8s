package com.viettran.reading_story_web.entity.mysql;

import java.time.Duration;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AvatarFrame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String imgSrc;

    @Builder.Default
    int price = 100;

    @Column(name = "usage_time")
    @Builder.Default
    Duration usageTime = Duration.ofHours(24);

    // relationship
    @OneToMany
    List<Inventory> inventory;
}
