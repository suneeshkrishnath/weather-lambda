package com.ik.aws.lambda.controller;

import com.ik.aws.lambda.dto.WorkoutSuggestionRequest;
import com.ik.aws.lambda.dto.WorkoutSuggestionResponse;
import com.ik.aws.lambda.dto.WeatherSnapshot;
import com.ik.aws.lambda.service.WeatherService;
import com.ik.aws.lambda.service.WeatherSnapshotService;
import com.ik.aws.lambda.service.WorkoutSuggestionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherApiIntegrationTests {

    @Test
    void weatherControllerExposesCurrentAndPredictionEndpoints() {
        WeatherService weatherService = new WeatherService();
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
        ReflectionTestUtils.setField(weatherService, "baseUrl", "https://example.com/weather");
        WeatherController controller = new WeatherController(weatherService);

        ResponseEntity<String> current = controller.current(10.0, 20.0);
        ResponseEntity<String> prediction = controller.prediction(10.0, 20.0);

        assertEquals("https://example.com/weather/current?lat=10.0&lon=20.0&appid=test-key", current.getBody());
        assertEquals("https://example.com/weather?lat=10.0&lon=20.0&appid=test-key", prediction.getBody());
    }

    @Test
    void workoutControllerReturnsSuggestionAndValidatesInput() {
        WorkoutSuggestionService workoutSuggestionService = new WorkoutSuggestionService();
        WeatherSnapshotService weatherSnapshotService = new WeatherSnapshotService() {
            @Override
            public WeatherSnapshot currentSnapshot(double lat, double lon) {
                return new WeatherSnapshot(20.0, 0.0, "Sunny");
            }
        };
        WorkoutController controller = new WorkoutController(workoutSuggestionService, weatherSnapshotService);

        ResponseEntity<WorkoutSuggestionResponse> response = controller.suggestion(new WorkoutSuggestionRequest("Berlin", "running"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Running", response.getBody().activity());
        assertEquals("Good time for a running session in Berlin.", response.getBody().recommendation());
        assertEquals(true, response.getBody().feasible());
    }

    @Test
    void workoutControllerRejectsBlankLocation() {
        WorkoutController controller = new WorkoutController(new WorkoutSuggestionService(), new WeatherSnapshotService());

        try {
            controller.suggestion(new WorkoutSuggestionRequest(" ", "running"));
        } catch (IllegalArgumentException ex) {
            assertEquals("location must not be blank", ex.getMessage());
        }
    }
}
