package com.vietanh.webmanh.dtos.events;

import com.vietanh.webmanh.constants.EventTopic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCreatedEvent extends BaseEvent {
    Integer userId;
    String email;
    String username;

    public UserCreatedEvent(Integer userId, String email, String username) {
        super(UUID.randomUUID().toString(), Instant.now(), 1, EventTopic.USER_CREATED.getTopicName());
        this.userId = userId;
        this.email = email;
        this.username = username;
    }
}