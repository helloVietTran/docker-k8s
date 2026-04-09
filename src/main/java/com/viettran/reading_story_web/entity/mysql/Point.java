package com.viettran.reading_story_web.entity.mysql;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "point")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    Integer total;
    Integer consecutiveAttendanceCount = 1;

    Instant lastAttendanceDate;
    // relationship

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "point")
    List<PointHistory> pointHistories;
}
