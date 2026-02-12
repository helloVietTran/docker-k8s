package com.vietanh.webmanh.dtos.events;

import com.vietanh.webmanh.constants.EventTopic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserForgotEvent extends BaseEvent {
    String email;
    String username;
    String resetUrl;

    public UserForgotEvent(String email, String username, String resetUrl) {
        super(UUID.randomUUID().toString(), Instant.now(), 1, EventTopic.USER_CREATED.getTopicName());
        this.email = email;
        this.username = username;
        this.resetUrl = resetUrl;
    }
}
