package com.vietanh.webmanh.dbs.postgres.models;

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
@Table(name = "chapter_image")
public class ChapterImage extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer chapterImageId;

    // convention : <slug_comic> + <slug_chapter> + slug_image
    @Column(nullable = false)
    String chapterImageSrc;

    @Column(nullable = false)
    String chapterImageName;

    @ManyToOne
    Chapter chapter;
}
