package com.ik.aws.lambda.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherServiceTests {

    @Test
    void fetchCurrentWeatherBuildsExpectedUrl() {
        WeatherService weatherService = new WeatherService();
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
        ReflectionTestUtils.setField(weatherService, "baseUrl", "https://example.com/weather");

        String result = weatherService.fetchCurrentWeather(1.2, 3.4);

        assertEquals("https://example.com/weather/current?lat=1.2&lon=3.4&appid=test-key", result);
    }

    @Test
    void fetchWeatherPredictionBuildsExpectedUrl() {
        WeatherService weatherService = new WeatherService();
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
        ReflectionTestUtils.setField(weatherService, "baseUrl", "https://example.com/weather");

        String result = weatherService.fetchWeatherPrediction(1.2, 3.4);

        assertEquals("https://example.com/weather?lat=1.2&lon=3.4&appid=test-key", result);
    }
}
