# Weather Lambda

A Spring Boot 3 application for weather-related APIs, designed for AWS Lambda and API Gateway deployment. The service exposes weather endpoints and workout suggestions for demo and testing use.

## Overview

This project provides:
- Current weather lookup by location
- Weather prediction for the next 5 hours by location
- Workout suggestion based on weather and activity
- Health and validation endpoints
- AWS-friendly Lambda deployment support

## Technology Stack

- Java 21
- Spring Boot 3.3.3
- Spring Web MVC
- AWS Lambda runtime support
- Maven

## Run locally

From the project root:

```bash
./mvnw spring-boot:run
```

Then call endpoints at:

```text
http://localhost:8080/api
```

## Base API paths

```text
/api/hello
/api/health
/api/weather/current
/api/weather/prediction
/api/workout/suggestion
```

## Endpoints and sample data

### 1) Hello endpoint

Request:

```bash
curl "http://localhost:8080/api/hello"
```

Response:

```json
{
  "message": "Hello World from weather-lambda",
  "status": "ok",
  "environment": "aws"
}
```

### 2) Health endpoint

Request:

```bash
curl "http://localhost:8080/api/health"
```

Response:

```json
{
  "status": "UP",
  "service": "weather-lambda"
}
```

### 3) Current weather by location

This is the preferred request format for real demo weather payloads.

Request:

```bash
curl "http://localhost:8080/api/weather/current?location=Wroclaw"
```

Example response:

```json
{
  "location": "Wroclaw",
  "temperatureCelsius": 33.0,
  "condition": "Sunny",
  "summary": "33°C and sunny in Wroclaw."
}
```

For Wroclaw, the current weather is intentionally returned as 33°C and sunny.

### 4) Weather prediction by location

Request:

```bash
curl "http://localhost:8080/api/weather/prediction?location=Wroclaw"
```

Example response:

```json
{
  "location": "Wroclaw",
  "predictions": [
    {
      "time": "20:35",
      "temperatureCelsius": 31.0,
      "condition": "Sunny"
    },
    {
      "time": "21:35",
      "temperatureCelsius": 29.0,
      "condition": "Cloudy"
    },
    {
      "time": "22:35",
      "temperatureCelsius": 31.0,
      "condition": "Cloudy"
    },
    {
      "time": "23:35",
      "temperatureCelsius": 33.0,
      "condition": "Rainy"
    },
    {
      "time": "00:35",
      "temperatureCelsius": 35.0,
      "condition": "Cloudy"
    }
  ]
}
```

### 5) Current weather by coordinates (legacy compatibility route)

Request:

```bash
curl "http://localhost:8080/api/weather/current?lat=52.52&lon=13.41"
```

Response:

```text
https://example.com/weather/current?lat=52.52&lon=13.41&appid=YOUR_API_KEY
```

### 6) Prediction by coordinates (legacy compatibility route)

Request:

```bash
curl "http://localhost:8080/api/weather/prediction?lat=52.52&lon=13.41"
```

Response:

```text
https://example.com/weather?lat=52.52&lon=13.41&appid=YOUR_API_KEY
```

### 7) Workout suggestion

Request:

```bash
curl "http://localhost:8080/api/workout/suggestion?location=Berlin&activity=running"
```

Response:

```json
{
  "activity": "Running",
  "recommendation": "Good time for a running session in Berlin.",
  "feasible": true
}
```

Another example:

```bash
curl "http://localhost:8080/api/workout/suggestion?location=Berlin&activity=cycling"
```

Response:

```json
{
  "activity": "Cycling",
  "recommendation": "Not ideal for cycling in Berlin due to rain and temperature 20.0C.",
  "feasible": false
}
```

## AWS deployment notes

The application is designed for AWS Lambda + API Gateway. The expected pattern is:

```text
https://<api-id>.execute-api.<region>.amazonaws.com/prod/api/hello
https://<api-id>.execute-api.<region>.amazonaws.com/prod/api/weather/current?location=Wroclaw
https://<api-id>.execute-api.<region>.amazonaws.com/prod/api/weather/prediction?location=Wroclaw
```

## Postman collection snippet

You can test the API in Postman by importing a raw JSON collection like this:

```json
{
  "info": {
    "name": "Weather Lambda API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Hello",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/hello"
      }
    },
    {
      "name": "Health",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/health"
      }
    },
    {
      "name": "Current Weather by Location",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/weather/current?location=Wroclaw",
          "host": ["{{baseUrl}}"],
          "path": ["api", "weather", "current"],
          "query": [
            { "key": "location", "value": "Wroclaw" }
          ]
        }
      }
    },
    {
      "name": "Weather Prediction by Location",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/weather/prediction?location=Wroclaw",
          "host": ["{{baseUrl}}"],
          "path": ["api", "weather", "prediction"],
          "query": [
            { "key": "location", "value": "Wroclaw" }
          ]
        }
      }
    },
    {
      "name": "Workout Suggestion",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/workout/suggestion?location=Berlin&activity=running",
          "host": ["{{baseUrl}}"],
          "path": ["api", "workout", "suggestion"],
          "query": [
            { "key": "location", "value": "Berlin" },
            { "key": "activity", "value": "running" }
          ]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    }
  ]
}
```

## AWS SAM deployment commands

From the project root (`weather-lambda`):

```bash
# Build the project
./mvnw clean package

# Package the SAM template
sam package template.yaml --output-template-file packaged.yaml --s3-bucket <your-s3-bucket-name>

# Deploy the packaged stack
sam deploy --template-file packaged.yaml --stack-name weather-lambda-stack --capabilities CAPABILITY_IAM --resolve-s3
```

If you want to validate the SAM template locally before deploy:

```bash
sam validate
```

If you want to build and run the local Lambda runtime for testing:

```bash
./mvnw spring-boot:run
```

## Logging configuration

Verbose Spring MVC logging is enabled for mapping inspection.

Application config includes:

```yaml
logging:
  level:
    root: INFO
    org:
      springframework:
        web: DEBUG
        web.servlet.mvc.method.annotation.RequestMappingHandlerMapping: TRACE
```

This produces trace output similar to:

```text
TRACE ... RequestMappingHandlerMapping :
  c.i.a.l.c.WeatherController:
    {GET [/api/weather/current], params [location]}: current(String)
    {GET [/api/weather/current], params [lat && lon]}: current(double,double)
```

## Notes

- The weather endpoints support both location-based and coordinate-based inputs.
- The location-based weather is meant for demo/test behavior, including Wroclaw returning 33°C and sunny.
- The lat/lon routes remain available for compatibility and earlier project behavior.
- The project is still suitable for serverless AWS deployment with Lambda and API Gateway integration.

## Project files

Key files:
- `src/main/java/com/ik/aws/lambda/controller/WeatherController.java`
- `src/main/java/com/ik/aws/lambda/controller/WorkoutController.java`
- `src/main/java/com/ik/aws/lambda/service/WeatherService.java`
- `src/main/java/com/ik/aws/lambda/dto/WeatherResponse.java`
- `src/main/java/com/ik/aws/lambda/dto/WeatherPredictionResponse.java`
- `src/main/resources/application.yaml`
- `template.yaml`
