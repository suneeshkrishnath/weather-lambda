package com.ik.aws.lambda.service;

import com.ik.aws.lambda.dto.WeatherSnapshot;
import com.ik.aws.lambda.dto.WorkoutSuggestionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkoutSuggestionService {

    public WorkoutSuggestionResponse suggest(String location, String activity, WeatherSnapshot weatherSnapshot) {
        log.info("Evaluating workout suggestion for location={}, activity={}, snapshot={}", location, activity, weatherSnapshot);
        try {
            String normalized = activity.toLowerCase();
            if (!"running".equals(normalized) && !"cycling".equals(normalized)) {
                throw new IllegalArgumentException("activity must be running or cycling");
            }

            boolean feasible = isFeasibleForActivity(normalized, weatherSnapshot);
            String recommendation = buildRecommendation(location, normalized, feasible, weatherSnapshot);
            return new WorkoutSuggestionResponse(capitalize(normalized), recommendation, feasible);
        } catch (Exception e) {
            log.error("Failed to generate workout suggestion for location={}, activity={}", location, activity, e);
            throw e;
        }
    }

    private boolean isFeasibleForActivity(String activity, WeatherSnapshot weatherSnapshot) {
        if (weatherSnapshot.precipitationMmPerHour() > 1.0) {
            return false;
        }
        double temperature = weatherSnapshot.temperatureCelsius();
        if ("running".equals(activity)) {
            return temperature >= 0 && temperature <= 30;
        }
        return temperature >= 5 && temperature <= 28;
    }

    private String buildRecommendation(String location, String activity, boolean feasible, WeatherSnapshot weatherSnapshot) {
        if (feasible) {
            return "Good time for a " + activity + " session in " + location + ".";
        }
        return "Not ideal for " + activity + " in " + location + " due to " + weatherSnapshot.condition().toLowerCase() + " and temperature " + weatherSnapshot.temperatureCelsius() + "C.";
    }

    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }
}
