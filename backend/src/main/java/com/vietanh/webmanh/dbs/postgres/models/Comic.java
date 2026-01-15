package com.vietanh.webmanh.dbs.postgres.models;

import com.vietanh.webmanh.constants.AdminDecision;
import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.ComicStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
public class Comic extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer comicId;

    @Column(nullable = false)
    String comicName;

    String otherName;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    ComicStatus status = ComicStatus.UPCOMING;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", nullable = false)
    List<String> coverSrc;

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

//    String newestChapter; // denormalization

    @Enumerated(EnumType.STRING)
    Gender gender; // null là cả 2

    @Builder.Default
    AdminDecision adminDecision =  AdminDecision.APPROVE_PENDING;

    @Column(nullable = false)
    String slug;

    // relationship
    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Chapter> chapters;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "author_id",
            foreignKey = @ForeignKey(name = "fk_comic_author"),
            nullable = true
    )
    User author;

    @ManyToMany
    Set<Genre> genres;

    public void generateSelfComicSlug() {
        if (this.comicName == null || this.comicName.isEmpty()) {
            return;
        }

        this.comicName = this.comicName.replaceAll("đ", "d").replaceAll("Đ", "D");

        String normalized = Normalizer.normalize(this.comicName, Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{M}", "");

        String slug = noDiacritics.replaceAll("[^a-zA-Z0-9\\s]", "");
        slug = slug.trim().replaceAll("\\s+", "-");

        this.slug = slug.toLowerCase();
    }
}


