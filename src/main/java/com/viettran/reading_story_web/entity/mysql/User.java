package com.viettran.reading_story_web.entity.mysql;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viettran.reading_story_web.entity.base.BaseEntity;
import com.viettran.reading_story_web.enums.Gender;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String email;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(nullable = false)
    String password;

    String imgSrc;

    @Builder.Default
    Boolean isVerified = false;

    Instant createdAt;
    Instant updatedAt;

    // relationship
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Comment> comment;

    @JsonManagedReference
    @OneToOne(mappedBy = "user")
    Level level;

    @JsonManagedReference
    @OneToOne(mappedBy = "user")
    Point point;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    List<Reaction> reaction;

    @OneToMany(mappedBy = "user")
    List<ErrorReporter> errorReporters;

    @OneToMany
    List<ResetPasswordToken> resetPasswordTokens;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name"))
    Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> follows;

    @OneToMany
    List<Inventory> inventory;

    @JsonManagedReference
    @OneToMany
    Set<ReadingHistory> readingHistories;
}
