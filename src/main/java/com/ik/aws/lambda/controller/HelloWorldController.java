package com.ik.aws.lambda.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/hello")
    public Map<String, Object> sayHello() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Hello World from weather-lambda");
        response.put("status", "ok");
        response.put("environment", "aws");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "weather-lambda");
        return response;
    }
}
