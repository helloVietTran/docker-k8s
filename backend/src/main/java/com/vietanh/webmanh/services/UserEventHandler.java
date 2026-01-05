package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.events.UserCreatedEvent;
import com.vietanh.webmanh.dtos.events.UserForgotEvent;

public interface UserEventHandler {
    void handleUserCreatedEvent(UserCreatedEvent event);
    void handleUserForgotEvent(UserForgotEvent event);
}
