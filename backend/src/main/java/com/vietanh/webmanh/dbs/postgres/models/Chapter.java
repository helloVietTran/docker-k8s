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
public class Chapter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer chapterId;

    @Column(nullable = false)
    String chapterNo;

    String chapterName;

    @Builder.Default
    int viewCount = 0;

    @Column(nullable = false)
    String slug;

    Integer chapterIndex;

    @ManyToOne
    Comic comic;

    @OneToMany(
            mappedBy = "chapter",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    List<ChapterImage> chapterImages;

    public void generateSelfSlug() {
        this.slug = "chap-" + this.chapterNo;
    }
}
