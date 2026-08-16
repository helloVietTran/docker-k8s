package com.viettran.reading_story_web.entity.mysql;

import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viettran.reading_story_web.entity.base.BaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    int chap;

    String chapterName;

    @Builder.Default
    int viewCount = 0;

    String slug;

    // relationship
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    Story story;

    @JsonManagedReference
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Comment> comments;

    @JsonManagedReference
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Image> images;

    @PrePersist
    public void onCreate() {
        slug = "chap-" + this.chap;
    }
}
