package com.vietanh.webmanh.dtos.events;

import com.vietanh.webmanh.constants.EventTopic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserVerifyRequestedEvent extends BaseEvent {

     String email;
     String username;
     String verifyToken;
     String verifyUrl;

    public UserVerifyRequestedEvent(
            String email,
            String username,
            String verifyToken,
            String verifyUrl) {

        super(UUID.randomUUID().toString(), Instant.now(), 1, EventTopic.USER_VERIFICATION_REQUESTED.getTopicName());
        this.email = email;
        this.username = username;
        this.verifyToken = verifyToken;
        this.verifyUrl = verifyUrl;
    }
}
