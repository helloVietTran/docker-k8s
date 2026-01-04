package com.vietanh.webmanh.services.handlers;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dtos.events.UserCreatedEvent;
import com.vietanh.webmanh.dtos.events.UserVerifyRequestedEvent;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.UserEventHandler;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

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
    public void handleUserVerifyRequestedEvent(UserVerifyRequestedEvent event) {
        sendVerifyEmail(event);
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
            throw new AppException(ErrorCode.SEND_WELCOME_MAIL_FAILED);
        }
    }

    private void sendVerifyEmail(UserVerifyRequestedEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("username", event.getUsername());
            context.setVariable("verifyUrl", event.getVerifyUrl());

            String htmlContent = templateEngine.process("verify-user", context);

            helper.setTo(event.getEmail());
            helper.setSubject("Xác thực tài khoản Webmanh");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new AppException(ErrorCode.SEND_WELCOME_MAIL_FAILED);
        }
    }

}
