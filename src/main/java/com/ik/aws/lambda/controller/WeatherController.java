package com.ik.aws.lambda.controller;

import com.ik.aws.lambda.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<String> current(@RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(weatherService.fetchCurrentWeather(lat, lon));
    }

    @GetMapping("/prediction")
    public ResponseEntity<String> prediction(@RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(weatherService.fetchWeatherPrediction(lat, lon));
    }
}
