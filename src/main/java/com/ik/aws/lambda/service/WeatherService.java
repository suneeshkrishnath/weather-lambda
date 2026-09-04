package com.ik.aws.lambda.service;

import com.ik.aws.lambda.dto.WeatherForecast;
import com.ik.aws.lambda.dto.WeatherPredictionResponse;
import com.ik.aws.lambda.dto.WeatherResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather.api.key:demo-key}")
    private String apiKey;

    @Value("${weather.api.base-url:https://example.com/weather}")
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

    public WeatherResponse fetchCurrentWeather(String location) {
        String normalizedLocation = normalizeLocation(location);
        log.info("Preparing current weather for location={}", normalizedLocation);
        return buildCurrentWeather(normalizedLocation);
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

    public WeatherPredictionResponse fetchWeatherPrediction(String location) {
        String normalizedLocation = normalizeLocation(location);
        log.info("Preparing weather prediction for location={}", normalizedLocation);
        return buildPrediction(normalizedLocation);
    }

    private WeatherResponse buildCurrentWeather(String location) {
        String city = normalizeLocation(location);
        double temperatureCelsius = resolveTemperature(city);
        String condition = resolveCondition(city, temperatureCelsius);
        return new WeatherResponse(city, temperatureCelsius, condition,
                String.format(Locale.ENGLISH, "%.0f°C and %s in %s.", temperatureCelsius, condition.toLowerCase(Locale.ROOT), city));
    }

    private String buildCurrentWeatherSummary(String location, double lat, double lon) {
        WeatherResponse current = buildCurrentWeather(location + "(" + lat + "," + lon + ")");
        return current.summary();
    }

    private WeatherPredictionResponse buildPrediction(String location) {
        String city = normalizeLocation(location);
        List<WeatherForecast> predictions = new ArrayList<>();
        double baseTemperature = resolveTemperature(city);
        String baseCondition = resolveCondition(city, baseTemperature);
        for (int hour = 1; hour <= 5; hour++) {
            double temperature = Math.round((baseTemperature + (hour - 2) * 2.0) * 10.0) / 10.0;
            String condition = hour % 3 == 0 && baseCondition.equalsIgnoreCase("Sunny") ? "Cloudy" : hour % 2 == 0 ? "Rainy" : baseCondition;
            String time = LocalDateTime.now().plusHours(hour).format(DateTimeFormatter.ofPattern("HH:mm"));
            predictions.add(new WeatherForecast(time, temperature, condition));
        }
        return new WeatherPredictionResponse(city, predictions);
    }

    private String buildPredictionSummary(String location, double lat, double lon) {
        WeatherPredictionResponse prediction = buildPrediction(location + "(" + lat + "," + lon + ")");
        return prediction.predictions().toString();
    }

    private double resolveTemperature(String location) {
        String normalized = normalizeLocation(location).toLowerCase(Locale.ROOT);
        if (normalized.contains("wro")) {
            return 33.0;
        }
        if (normalized.contains("berlin")) {
            return 24.0;
        }
        if (normalized.contains("london")) {
            return 19.0;
        }
        if (normalized.contains("paris")) {
            return 26.0;
        }
        int seed = normalized.chars().sum();
        return 15.0 + (seed % 18);
    }

    private String resolveCondition(String location, double temperature) {
        String normalized = normalizeLocation(location).toLowerCase(Locale.ROOT);
        if (normalized.contains("wro")) {
            return "Sunny";
        }
        if (temperature >= 30) {
            return "Sunny";
        }
        if (temperature >= 20) {
            return "Cloudy";
        }
        if (normalized.contains("berlin") || normalized.contains("london") || normalized.contains("paris")) {
            return "Rainy";
        }
        return "Cloudy";
    }

    private String normalizeLocation(String location) {
        if (location == null || location.isBlank()) {
            return "Wroclaw";
        }
        return location.trim();
    }
}
