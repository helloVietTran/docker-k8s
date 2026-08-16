package com.viettran.reading_story_web.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryResponse {
    String id;
    UserResponse user;
    AvatarFrameResponse avatarFrame;

    String createdAt;
    String expirationDate;
}
