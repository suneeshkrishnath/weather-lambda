package com.ik.aws.lambda.dto;

import java.util.List;

public record WeatherPredictionResponse(String location, List<WeatherForecast> predictions) {
}
