package com.ik.aws.lambda.controller;

import com.ik.aws.lambda.dto.WorkoutSuggestionRequest;
import com.ik.aws.lambda.dto.WorkoutSuggestionResponse;
import com.ik.aws.lambda.service.WeatherSnapshotService;
import com.ik.aws.lambda.service.WorkoutSuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutSuggestionService workoutSuggestionService;
    private final WeatherSnapshotService weatherSnapshotService;

    @GetMapping("/suggestion")
    public ResponseEntity<WorkoutSuggestionResponse> suggestion(WorkoutSuggestionRequest request) {
        log.info("Received workout suggestion request for location={}, activity={}", request.location(), request.activity());
        try {
            var snapshot = weatherSnapshotService.currentSnapshot(request.location().hashCode(), request.location().length());
            return ResponseEntity.ok(workoutSuggestionService.suggest(request.location(), request.activity(), snapshot));
        } catch (Exception e) {
            log.error("Error while generating workout suggestion for location={}, activity={}", request.location(), request.activity(), e);
            throw e;
        }
    }
}
