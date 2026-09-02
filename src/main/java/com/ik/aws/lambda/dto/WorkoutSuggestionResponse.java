package com.ik.aws.lambda.dto;

public record WorkoutSuggestionResponse(String activity, String recommendation, boolean feasible) {
}
