package com.vietanh.webmanh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReadingStoryWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadingStoryWebApplication.class, args);
	}

}
