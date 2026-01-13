package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "reading_history")
public class ReadingHistory extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer readingHistoryId;

    @Column(nullable = false)
    Integer userId;

    @Column(nullable = false)
    Integer comicId;

    @Column(columnDefinition = "integer[]")
    List<Integer> readChapters = new ArrayList<>();
}
