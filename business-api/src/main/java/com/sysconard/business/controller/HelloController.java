package com.sysconard.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller principal da Business API
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@RestController
@RequestMapping("/")
public class HelloController {

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Glojas Business API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        response.put("description", "API principal com lógica de negócio - Sistema Glojas");
        response.put("endpoints", Map.of(
            "health", "/actuator/health",
            "info", "/actuator/info",
            "hello", "/hello"
        ));
        return response;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World from Business API! 🚀");
        response.put("service", "Glojas Business API");
        response.put("java_version", System.getProperty("java.version"));
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Business API");
        response.put("database", "PostgreSQL");
        response.put("port", 8081);
        response.put("context_path", "/api/business");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}