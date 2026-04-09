package com.viettran.reading_story_web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.viettran.reading_story_web.dto.request.EmailRequest;
import com.viettran.reading_story_web.dto.request.Receiver;
import com.viettran.reading_story_web.dto.request.SendEmailRequest;
import com.viettran.reading_story_web.dto.request.Sender;
import com.viettran.reading_story_web.dto.response.EmailResponse;
import com.viettran.reading_story_web.entity.mysql.User;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.repository.httpClient.EmailClient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;
    TemplateEngine templateEngine;

    @NonFinal
    @Value("${app.frontend.url}")
    String FRONTEND_URL;

    @Value("${app.brevo.apikey}")
    @NonFinal
    String apiKey;

    @Value("${app.admin-email}")
    @NonFinal
    String adminEmail;

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder().name("Vie truyen").email(adminEmail).build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

    public EmailResponse sendResetPasswordEmail(String token, User user) {
        String resetLink = FRONTEND_URL + "/reset-password?ticket=" + token + "&uid=" + user.getId();

        Context context = new Context();
        context.setVariable("username", user.getName());
        context.setVariable("resetLink", resetLink);
        String htmlContent = templateEngine.process("reset-password-email.html", context);

        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder().name("Vie truyen").email(adminEmail).build())
                .to(List.of(Receiver.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build()))
                .htmlContent(htmlContent)
                .subject("Yêu cầu thay đổi mật khẩu")
                .build();
        try {
            return emailClient.sendEmail(apiKey, emailRequest);

        } catch (FeignException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
