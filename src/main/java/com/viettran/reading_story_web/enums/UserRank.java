package com.viettran.reading_story_web.enums;

import lombok.Getter;

@Getter
public enum UserRank {
    RANK_1("Luyện khí", 0, 1),
    RANK_2("Trúc cơ", 10, 2),
    RANK_3("Kết đan", 100, 3),
    RANK_4("Nguyên anh", 500, 4),
    RANK_5("Hoá thần", 1000, 5),
    RANK_6("Luyện hư", 3000, 6),
    RANK_7("Hợp thể kì", 5000, 7),
    RANK_8("Độ kiếp kì", 7000, 8),
    RANK_9("Đại thừa kì", 10000, 9);

    private final String rankName;
    private final int chaptersRequired;
    private final int level;

    UserRank(String rankName, int chaptersRequired, int level) {
        this.rankName = rankName;
        this.chaptersRequired = chaptersRequired;
        this.level = level;
    }

    public static UserRank getRankByChaptersRead(int chaptersRead) {
        for (UserRank rank : UserRank.values()) {
            if (chaptersRead >= rank.getChaptersRequired()) {
                return rank;
            }
        }
        return RANK_1;
    }
}
