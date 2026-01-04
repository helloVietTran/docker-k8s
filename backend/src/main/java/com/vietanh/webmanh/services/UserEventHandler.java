package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.events.UserCreatedEvent;
import com.vietanh.webmanh.dtos.events.UserVerifyRequestedEvent;

public interface UserEventHandler {
    void handleUserCreatedEvent(UserCreatedEvent event);
    void handleUserVerifyRequestedEvent(UserVerifyRequestedEvent event);
}
