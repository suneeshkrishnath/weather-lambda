package com.ik.aws.lambda.dto;

public record WorkoutSuggestionRequest(
        String location,
        String activity) {
}
