package com.vietanh.webmanh.dbs.postgres.models;

import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.StoryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comic")
public class Comic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer comicId;

    @Column(nullable = false)
    String comicName;

    String otherName;
    // authorName

    @Enumerated(EnumType.STRING)
    StoryStatus status;

    @Column(nullable = false)
    String comicSrc;

    @Column(columnDefinition = "TEXT")
    String description = "";

    @Builder.Default
    int viewCount = 0;

    @Builder.Default
    int totalRatingPoint = 0;

    @Builder.Default
    double ratingPoint = 0;

    @Builder.Default
    int ratingCount = 0;

    @Builder.Default
    int commentCount = 0;

    @Builder.Default
    int followerCount = 0;

    @Builder.Default
    int likeCount = 0;

    @Enumerated(EnumType.STRING)
    Gender gender; // null là cả 2

    @Builder.Default
    int newestChapter = 0;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Chapter> chapters;

    @ManyToMany
    Set<Genre> genres;

    String slug;

    @PrePersist
    public void generateSlug() {
        this.slug = convertToSlug(this.comicName);
    }

    String convertToSlug(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        input = input.replaceAll("đ", "d").replaceAll("Đ", "D");

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{M}", "");

        String slug = noDiacritics.replaceAll("[^a-zA-Z0-9\\s]", "");
        slug = slug.trim().replaceAll("\\s+", "-");

        return slug.toLowerCase();
    }
}


