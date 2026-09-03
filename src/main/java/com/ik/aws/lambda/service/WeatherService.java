package com.ik.aws.lambda.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    public String fetchCurrentWeather(double lat, double lon) {
        log.info("Preparing current weather request for lat={}, lon={}", lat, lon);
        try {
            return baseUrl + "/current?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        } catch (Exception e) {
            log.error("Failed to prepare current weather request for lat={}, lon={}", lat, lon, e);
            throw e;
        }
    }

    public String fetchWeatherPrediction(double lat, double lon) {
        log.info("Preparing weather prediction request for lat={}, lon={}", lat, lon);
        try {
            return baseUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        } catch (Exception e) {
            log.error("Failed to prepare weather prediction request for lat={}, lon={}", lat, lon, e);
            throw e;
        }
    }
}
