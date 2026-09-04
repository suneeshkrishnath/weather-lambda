package com.ik.aws.lambda.dto;

public record WeatherResponse(String location, double temperatureCelsius, String condition, String summary) {
}
