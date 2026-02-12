package com.vietanh.webmanh.configs;


import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LoadOpenCV {
    static {
        OpenCV.loadLocally();
    }
}

