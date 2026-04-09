package com.viettran.reading_story_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryConfig {
    @NonFinal
    @Value("${app.cloudinary.cloud-name}")
    String cloudName;

    @NonFinal
    @Value("${app.cloudinary.api-key}")
    String apiKey;

    @NonFinal
    @Value("${app.cloudinary.api-secret}")
    String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dlfrltm8x",
                "api_key", "357454587825522",
                "api_secret", "wBBGGx44RolTSHoes5bsDWxmce4"));
    }
}
