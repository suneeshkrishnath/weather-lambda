package com.ik.aws.lambda.controller;

import com.ik.aws.lambda.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<String> current(@RequestParam double lat, @RequestParam double lon) {
        log.info("Received current weather request for lat={}, lon={}", lat, lon);
        try {
            return ResponseEntity.ok(weatherService.fetchCurrentWeather(lat, lon));
        } catch (Exception e) {
            log.error("Error while fetching current weather for lat={}, lon={}", lat, lon, e);
            throw e;
        }
    }

    @GetMapping("/prediction")
    public ResponseEntity<String> prediction(@RequestParam double lat, @RequestParam double lon) {
        log.info("Received weather prediction request for lat={}, lon={}", lat, lon);
        try {
            return ResponseEntity.ok(weatherService.fetchWeatherPrediction(lat, lon));
        } catch (Exception e) {
            log.error("Error while fetching weather prediction for lat={}, lon={}", lat, lon, e);
            throw e;
        }
    }
}
