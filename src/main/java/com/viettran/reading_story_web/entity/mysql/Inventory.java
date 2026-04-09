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
@Table(name = "inventory")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Inventory extends ExpirationBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // relationship
    @ManyToOne
    User user;

    @ManyToOne
    AvatarFrame avatarFrame;

    public Inventory(User user, AvatarFrame avatarFrame, int seconds) {
        super(seconds);
        this.user = user;
        this.avatarFrame = avatarFrame;
    }
}
