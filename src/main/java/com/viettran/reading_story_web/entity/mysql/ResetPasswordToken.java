package com.viettran.reading_story_web.entity.mysql;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.viettran.reading_story_web.entity.base.ExpirationBaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "reset_password_token")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ResetPasswordToken extends ExpirationBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String token;
    String status; // used or unused

    // relationship
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public ResetPasswordToken(User user, int seconds) {
        super(seconds);
        this.user = user;
    }
}
