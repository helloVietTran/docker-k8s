package com.vietanh.webmanh.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.Instant;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() throws Exception {
        return String.format(
                "Hello World | host=%s | time=%s",
                InetAddress.getLocalHost().getHostName(),
                Instant.now()
        );
    }
}