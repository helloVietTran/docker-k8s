package com.viettran.reading_story_web.entity.mysql;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.viettran.reading_story_web.entity.base.BaseEntity;
import com.viettran.reading_story_web.enums.PointHistoryType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "point-history")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    PointHistoryType type;

    String description;

    Integer pointFluctuation;

    // relationship
    @JsonBackReference
    @ManyToOne
    Point point;
}
