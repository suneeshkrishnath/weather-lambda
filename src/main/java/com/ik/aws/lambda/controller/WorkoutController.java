package com.ik.aws.lambda.controller;

import com.ik.aws.lambda.dto.WorkoutSuggestionRequest;
import com.ik.aws.lambda.dto.WorkoutSuggestionResponse;
import com.ik.aws.lambda.service.WeatherSnapshotService;
import com.ik.aws.lambda.service.WorkoutSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutSuggestionService workoutSuggestionService;
    private final WeatherSnapshotService weatherSnapshotService;

    @GetMapping("/suggestion")
    public ResponseEntity<WorkoutSuggestionResponse> suggestion(WorkoutSuggestionRequest request) {
        return ResponseEntity.ok(workoutSuggestionService.suggest(request.location(), request.activity(), weatherSnapshotService.currentSnapshot(request.location().hashCode(), request.location().length())));
    }
}
