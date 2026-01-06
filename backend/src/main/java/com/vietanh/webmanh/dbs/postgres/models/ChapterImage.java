package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chapter_image")
public class ChapterImage extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String chapterImageId;

    // convention : <slug_comic> + <slug_chapter> + id
    @Column(nullable = false)
    String imageSrc;

    @ManyToOne
    Chapter chapter;
}
