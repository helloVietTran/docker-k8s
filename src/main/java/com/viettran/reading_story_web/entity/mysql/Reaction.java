package com.viettran.reading_story_web.entity.mysql;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viettran.reading_story_web.enums.ReactionType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "reaction")
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @JsonBackReference
    @ManyToOne
    User user;

    @JsonBackReference
    @ManyToOne
    Story story;

    @JsonBackReference
    @ManyToOne
    Comment comment;

    @Enumerated(EnumType.STRING)
    ReactionType reactionType;
}
