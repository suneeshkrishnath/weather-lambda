package com.ik.aws.lambda.controller;

import com.ik.aws.lambda.dto.WorkoutSuggestionRequest;
import com.ik.aws.lambda.dto.WorkoutSuggestionResponse;
import com.ik.aws.lambda.dto.WeatherSnapshot;
import com.ik.aws.lambda.service.WeatherSnapshotService;
import com.ik.aws.lambda.service.WorkoutSuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workout")
public class WorkoutController {

    private final WorkoutSuggestionService workoutSuggestionService;
    private final WeatherSnapshotService weatherSnapshotService;

    public WorkoutController(WorkoutSuggestionService workoutSuggestionService, WeatherSnapshotService weatherSnapshotService) {
        this.workoutSuggestionService = workoutSuggestionService;
        this.weatherSnapshotService = weatherSnapshotService;
    }

    @GetMapping("/suggestion")
    public ResponseEntity<WorkoutSuggestionResponse> suggestion(WorkoutSuggestionRequest request) {
        if (request.location() == null || request.location().isBlank()) {
            throw new IllegalArgumentException("location must not be blank");
        }
        if (request.activity() == null || request.activity().isBlank()) {
            throw new IllegalArgumentException("activity must not be blank");
        }
        WeatherSnapshot weatherSnapshot = weatherSnapshotService.currentSnapshot(hashLocation(request.location()), request.location().length());
        return ResponseEntity.ok(workoutSuggestionService.suggest(request.location(), request.activity(), weatherSnapshot));
    }

    private double hashLocation(String location) {
        return Math.abs(location.hashCode() % 90);
    }
}
