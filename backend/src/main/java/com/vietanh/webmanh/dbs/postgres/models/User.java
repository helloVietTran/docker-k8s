package com.vietanh.webmanh.dbs.postgres.models;

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
@Table(name = "users") // rename
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

    String avatar;

    @Builder.Default
    Boolean isVerified = false; // xác minh mail thì chuyển thành true

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name"))
    Set<Role> roles;
}