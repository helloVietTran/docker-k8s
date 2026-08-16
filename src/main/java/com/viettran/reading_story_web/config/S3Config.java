package com.viettran.reading_story_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    @Value("${app.s3.region:${AWS_REGION:ap-southeast-1}}")
    private String region;

    @Value("${app.s3.access-key:${AWS_ACCESS_KEY_ID:}}")
    private String accessKey;

    @Value("${app.s3.secret-key:${AWS_SECRET_ACCESS_KEY:}}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        var builder = S3Client.builder().region(Region.of(region));

        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
        }

        return builder.build();
    }
}
