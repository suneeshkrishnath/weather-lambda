package com.ik.aws.lambda.dto;

public record WeatherSnapshot(double temperatureCelsius, double precipitationMmPerHour, String condition) {
}
