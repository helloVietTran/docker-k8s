package com.viettran.reading_story_web.entity.mysql;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viettran.reading_story_web.entity.base.BaseEntity;
import com.viettran.reading_story_web.enums.Gender;
import com.viettran.reading_story_web.enums.StoryStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "story")
public class Story extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String name;

    String otherName;
    String authorName;

    @Enumerated(EnumType.STRING)
    StoryStatus status;

    @Column(columnDefinition = "TEXT")
    String description = "";

    @Column(nullable = false)
    String imgSrc;

    @Builder.Default
    int viewCount = 0;

    @Builder.Default
    int totalRatingPoint = 0;

    @Builder.Default
    double rate = 0;

    @Builder.Default
    int ratingCount = 0;

    @Builder.Default
    int follower = 0;

    @Builder.Default
    int commentCount = 0;

    String slug;

    @Builder.Default
    Long likeCount = 0L;

    @Builder.Default
    int newestChapter = 0;

    // tính toán độ hot
    @Builder.Default
    boolean hot = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    Gender gender = Gender.BOTH;

    // relationship
    @JsonManagedReference
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Chapter> chapters;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Reaction> reactions;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "story_genre",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Genre> genres;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Follow> follows;

    @JsonManagedReference
    @OneToMany
    Set<ReadingHistory> readingHistories;

    @PrePersist
    public void generateSlug() {
        this.slug = convertToSlug(this.name);
    }

    String convertToSlug(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        // chuẩn hóa chữ đ
        input = input.replaceAll("đ", "d").replaceAll("Đ", "D");

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{M}", "");

        String slug = noDiacritics.replaceAll("[^a-zA-Z0-9\\s]", "");
        slug = slug.trim().replaceAll("\\s+", "-");

        return slug.toLowerCase();
    }
}
