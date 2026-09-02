package com.ik.aws.lambda.controller;

import com.ik.aws.lambda.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WeatherControllerTests {

    @Test
    void currentReturnsServiceResponse() {
        WeatherService weatherService = mock(WeatherService.class);
        when(weatherService.fetchCurrentWeather(10.0, 20.0)).thenReturn("current-weather");
        WeatherController controller = new WeatherController(weatherService);

        ResponseEntity<String> response = controller.current(10.0, 20.0);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("current-weather", response.getBody());
    }

    @Test
    void predictionReturnsServiceResponse() {
        WeatherService weatherService = mock(WeatherService.class);
        when(weatherService.fetchWeatherPrediction(10.0, 20.0)).thenReturn("prediction-weather");
        WeatherController controller = new WeatherController(weatherService);

        ResponseEntity<String> response = controller.prediction(10.0, 20.0);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("prediction-weather", response.getBody());
    }
}
