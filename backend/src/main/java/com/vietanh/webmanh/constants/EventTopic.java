package com.vietanh.webmanh.constants;

import lombok.Getter;

// convention: <domain>.<service>.<event>.<env>
@Getter
public enum EventTopic {

    USER_CREATED("user.created"),
    USER_FORGOT_EVENT("user.forgot");

    private final String topicName;

    EventTopic(String topicName) {
        this.topicName = topicName;
    }
}

