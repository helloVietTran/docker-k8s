package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.models.VerifyAccountToken;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.VerifyTokenRepository;
import com.vietanh.webmanh.dtos.events.UserCreatedEvent;
import com.vietanh.webmanh.dtos.events.UserForgotEvent;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.MailService;
import com.vietanh.webmanh.services.UserEventHandler;
import com.vietanh.webmanh.utils.AuthUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements MailService {
    VerifyTokenRepository verifyTokenRepository;
    UserRepository userRepository;

    UserEventHandler userEventHandler;

    JavaMailSender mailSender;
    SpringTemplateEngine templateEngine;

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

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("verifyUrl", verifyUrl);

            String htmlContent = templateEngine.process("verify-user", context);

            helper.setTo(user.getEmail());
            helper.setSubject("Xác thực tài khoản Webmanh");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

    // consumer
    @KafkaListener(
            topics = "#{T(com.vietanh.webmanh.constants.EventTopic).USER_CREATED.getTopicName()}"
    )
    public void listenUserCreatedEvent(UserCreatedEvent event) {
        userEventHandler.handleUserCreatedEvent(event);
    }

    @KafkaListener(
            topics = "#{T(com.vietanh.webmanh.constants.EventTopic).USER_FORGOT_EVENT.getTopicName()}"
    )
    public void listenUserForgotEvent(UserForgotEvent event) {
        userEventHandler.handleUserForgotEvent(event);
    }
}
