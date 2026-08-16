package com.viettran.reading_story_web.entity.mysql;

import java.time.Instant;

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
@Table(name = "error-reporter")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ErrorReporter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    String storyName;

    @Column(nullable = false)
    int atChapter;

    @Column(nullable = false)
    String type;

    @Column(nullable = false)
    String description;

    Instant createdAt;
    // relationship
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }
}
