package com.vietanh.webmanh.services.handlers;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dtos.events.UserCreatedEvent;
import com.vietanh.webmanh.dtos.events.UserForgotEvent;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.UserEventHandler;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventHandlerImpl implements UserEventHandler {
    JavaMailSender mailSender;
    SpringTemplateEngine templateEngine;

    @Override
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        sendWelcomeEmail(event);
    }

    @Override
    public void handleUserForgotEvent(UserForgotEvent event) {
        sendChangePasswordMail(event);
    }

    private void sendWelcomeEmail(UserCreatedEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("username", event.getUsername());
            context.setVariable("email", event.getEmail());

            String htmlContent = templateEngine.process("welcome-new-user", context);

            helper.setTo(event.getEmail());
            helper.setSubject("Chào mừng bạn đến với WebManh 🎉");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            log.warn(
                    "Failed to send welcome email | username={} | email={}",
                    event.getUsername(),
                    event.getEmail(),
                    e
            );
            throw new AppException(ErrorCode.SEND_WELCOME_MAIL_FAILED);
        }
    }

    private void sendChangePasswordMail(UserForgotEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("username", event.getUsername());
            context.setVariable("resetUrl", event.getResetUrl());

            String htmlContent = templateEngine.process("change-password-with-reset-token", context);

            helper.setTo(event.getEmail());
            helper.setSubject("Yêu cầu thay đổi tài khoản");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
