package com.vietanh.webmanh.constants;

import lombok.Getter;

// convention: <domain>.<service>.<event>.<env>
@Getter
public enum EventTopic {

    USER_CREATED("user.created"),
    USER_VERIFICATION_REQUESTED("user.verify_requested");

    private final String topicName;

    EventTopic(String topicName) {
        this.topicName = topicName;
    }
}

