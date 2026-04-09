package com.viettran.reading_story_web.entity.mysql;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viettran.reading_story_web.enums.UserRank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    @Builder.Default
    String rankName = UserRank.RANK_1.getRankName();

    @Column(nullable = false)
    @Builder.Default
    int level = UserRank.RANK_1.getLevel();

    @Column(nullable = false)
    @Builder.Default
    float process = 0.0f; // progress toward next rank

    @Column(nullable = false)
    @Builder.Default
    int chaptersRead = 0;

    @Builder.Default
    int nextLevelChaptersRequired = UserRank.RANK_2.getChaptersRequired();

    // relationship
    @JsonBackReference
    @OneToOne
    User user;

    public void increaseChaptersRead(int additionalChapters) {
        this.chaptersRead += additionalChapters;

        UserRank currentRank = UserRank.getRankByChaptersRead(this.chaptersRead);
        UserRank nextRank = getNextRank(currentRank);

        this.level = currentRank.getLevel();
        this.rankName = currentRank.getRankName();

        if (nextRank != null) {
            int currentThreshold = currentRank.getChaptersRequired();
            int nextThreshold = nextRank.getChaptersRequired();

            this.process = (float) (this.chaptersRead - currentThreshold) / (nextThreshold - currentThreshold);

            if (this.chaptersRead >= nextThreshold) {
                this.level = nextRank.getLevel();
                this.rankName = nextRank.getRankName();
                this.process = 0.0f;
            }

            this.nextLevelChaptersRequired = nextThreshold;
        } else {
            // Already at highest rank
            this.process = 1.0f;
            this.nextLevelChaptersRequired = Integer.MAX_VALUE;
        }
    }

    private UserRank getNextRank(UserRank rank) {
        UserRank[] ranks = UserRank.values();
        int index = rank.ordinal();
        if (index < ranks.length - 1) {
            return ranks[index + 1];
        }
        return null;
    }
}
