package com.ik.aws.lambda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    public String fetchCurrentWeather(double lat, double lon) {
        return baseUrl + "/current?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
    }

    public String fetchWeatherPrediction(double lat, double lon) {
        return baseUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
    }
}
