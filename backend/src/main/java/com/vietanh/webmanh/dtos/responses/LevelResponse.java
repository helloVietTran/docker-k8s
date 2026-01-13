package com.vietanh.webmanh.dtos.responses;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LevelResponse {
    Integer levelId;
    String rankName;
    float process;

    int chaptersRead;
    int nextLevelChaptersRequired;
}
