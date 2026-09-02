package com.ik.aws.lambda.service;

import com.ik.aws.lambda.dto.WeatherSnapshot;
import com.ik.aws.lambda.dto.WorkoutSuggestionResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutSuggestionServiceTests {

    @Test
    void runningIsFeasibleInMildDryWeather() {
        WorkoutSuggestionService service = new WorkoutSuggestionService();

        WorkoutSuggestionResponse response = service.suggest(
                "Berlin",
                "running",
                new WeatherSnapshot(18.0, 0.0, "Sunny"));

        assertTrue(response.feasible());
        assertEquals("Running", response.activity());
    }

    @Test
    void cyclingIsNotFeasibleInHeavyRain() {
        WorkoutSuggestionService service = new WorkoutSuggestionService();

        WorkoutSuggestionResponse response = service.suggest(
                "Berlin",
                "cycling",
                new WeatherSnapshot(20.0, 2.0, "Rain"));

        assertFalse(response.feasible());
        assertEquals("Not ideal for cycling in Berlin due to rain and temperature 20.0C.", response.recommendation());
    }
}
