package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.constants.EventTopic;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.models.VerifyAccountToken;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.VerifyTokenRepository;
import com.vietanh.webmanh.dtos.events.UserCreatedEvent;
import com.vietanh.webmanh.dtos.events.UserVerifyRequestedEvent;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.MailService;
import com.vietanh.webmanh.services.UserEventHandler;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.EventPublisher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements MailService {
    VerifyTokenRepository verifyTokenRepository;
    UserRepository userRepository;

    EventPublisher eventPublisher;
    UserEventHandler userEventHandler;

    @NonFinal
    @Value("${app.frontend.base-url}")
    private String baseUrl;

    @Override
    public void sendVerifyAccountMail() {
        Integer userId = AuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        Instant now = Instant.now();
        VerifyAccountToken token = VerifyAccountToken.builder()
                .createdAt(now)
                .expiryAt(now.plusSeconds(60*15))
                .userId(userId)
                .verifyToken(UUID.randomUUID().toString())
                .build();

        verifyTokenRepository.save(token);

        String verifyUrl = baseUrl + "/verify?verifyToken=" + token.getVerifyToken();
        UserVerifyRequestedEvent event =
                new UserVerifyRequestedEvent(user.getEmail(), user.getUsername(), token.getVerifyToken(), verifyUrl);
        eventPublisher.publish(EventTopic.USER_VERIFICATION_REQUESTED.getTopicName(), event);
    }

    // consumer
    @KafkaListener(
            topics = "#{T(com.vietanh.webmanh.constants.EventTopic).USER_CREATED.getTopicName()}"
    )
    public void listenUserCreatedEvent(UserCreatedEvent event) {
        userEventHandler.handleUserCreatedEvent(event);
    }

    @KafkaListener(
            topics = "#{T(com.vietanh.webmanh.constants.EventTopic).USER_CREATED.getTopicName()}"
    )
    public void listenUserVerifyRequestedEvent(UserCreatedEvent event) {
        userEventHandler.handleUserCreatedEvent(event);
    }
}
