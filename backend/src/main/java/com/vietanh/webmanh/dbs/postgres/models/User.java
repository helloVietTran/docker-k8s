package com.vietanh.webmanh.dbs.postgres.models;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.vietanh.webmanh.constants.Gender;
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
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer userId;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String email;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(nullable = false)
    String password;

    LocalDate dob;

    String avatar;

    @Builder.Default
    Boolean isVerified = false; // xác minh mail thì chuyển thành true

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name"))
    Set<Role> roles;

    @OneToMany(mappedBy = "author")
    Set<Comic> comics;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    Set<Comment> comments = new HashSet<>();
}