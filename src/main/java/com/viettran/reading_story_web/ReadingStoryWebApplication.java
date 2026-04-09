package com.viettran.reading_story_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@SpringBootApplication
@EnableScheduling
public class ReadingStoryWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadingStoryWebApplication.class, args);
    }
}
