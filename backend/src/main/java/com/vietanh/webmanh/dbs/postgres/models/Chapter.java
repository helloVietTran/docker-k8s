package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer chapterId;

    @Column(nullable = false)
    String chapterNumber;

    String chapterName;

    @Builder.Default
    int viewCount = 0;

    String slug;

    @ManyToOne
    Comic comic;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<ChapterImage> images;

    @PrePersist
    public void onCreate() {
        slug = "chap-" + this.chapterNumber;
    }
}
