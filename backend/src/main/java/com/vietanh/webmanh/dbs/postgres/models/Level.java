package com.vietanh.webmanh.dbs.postgres.models;

import com.vietanh.webmanh.constants.UserRank;
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
@Table(name = "level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer levelId;

    @Column(nullable = false)
    @Builder.Default
    String rankName = UserRank.RANK_1.getRankName();

    @Column(nullable = false)
    @Builder.Default
    float process = 0.0f; // progress toward next rank

    @Column(nullable = false)
    @Builder.Default
    int chaptersRead = 0;

    @Builder.Default
    int nextLevelChaptersRequired = UserRank.RANK_2.getReadChaptersRequired();

    @Column(nullable = false)
    Integer userId;

    public void increaseChaptersRead(int quantity) {
        this.chaptersRead += quantity;

        UserRank currentRank = UserRank.getRankByChaptersRead(this.chaptersRead);
        UserRank nextRank = UserRank.getNextRank(currentRank);

        this.rankName = currentRank.getRankName();

        if (nextRank != null) {
            int currentThreshold = currentRank.getReadChaptersRequired();
            int nextThreshold = nextRank.getReadChaptersRequired();

            this.process = (float) (this.chaptersRead - currentThreshold) / (nextThreshold - currentThreshold);

            if (this.chaptersRead >= nextThreshold) {
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
}