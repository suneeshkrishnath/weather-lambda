# Copilot Instructions

## Overview
This document provides guidelines and instructions for using GitHub Copilot effectively in the Weather Notification Service project. It outlines best practices, tips, and examples to maximize productivity and ensure high-quality code generation.

---

## Best Practices

### 1. **Write Clear Prompts**
- Use descriptive and specific prompts to guide Copilot.
- Example:
  - **Good Prompt**: "Create a Spring Boot REST controller for fetching current weather."
  - **Bad Prompt**: "Write some code for weather."

### 2. **Iterate and Refine**
- Review Copilot's suggestions and refine your prompts if needed.
- Use comments to provide additional context or constraints.

### 3. **Leverage Context**
- Ensure that Copilot has access to relevant files and project structure.
- Open related files in the editor to provide context for better suggestions.

### 4. **Validate and Test**
- Always validate Copilot's code suggestions.
- Write unit and integration tests to ensure correctness.

---

## Examples

### 1. **Service Layer**
Prompt:
```java
// Write a service method to fetch current weather data from OpenWeatherMap API.
```
Expected Output:
```java
@Service
public class WeatherService {
    public WeatherData fetchCurrentWeather(String location) {
        // Call OpenWeatherMap API and parse the response
        return new WeatherData(...);
    }
}
```

### 2. **Controller Layer**
Prompt:
```java
// Create a REST controller for weather endpoints.
```
Expected Output:
```java
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @GetMapping("/current")
    public ResponseEntity<WeatherData> getCurrentWeather(@RequestParam String location) {
        WeatherData data = weatherService.fetchCurrentWeather(location);
        return ResponseEntity.ok(data);
    }
}
```

---

## Tips
- Use Copilot for boilerplate code generation, such as DTOs, controllers, and configuration files.
- Combine Copilot with manual coding for complex logic and critical sections.
- Regularly update the project dependencies and ensure compatibility with Copilot's suggestions.

---

## Limitations
- Copilot may not always generate optimal or secure code.
- Suggestions may require customization to fit the project's specific requirements.
- Avoid relying solely on Copilot for critical or sensitive code.

---

## Additional References

### 1. **General Instructions**
- Refer to the `GENERAL_INSTRUCTIONS.md` file located in the `docs/instructions` directory for project setup, development workflow, and collaboration practices.

### 2. **Coding Standards**
- Refer to the `CODING_INSTRUCTIONS.md` file located in the `docs/instructions` directory for coding standards and best practices, including error handling, POJO creation, and testing guidelines.

---

## Conclusion
GitHub Copilot is a powerful tool to enhance productivity and streamline development. By following these instructions and best practices, you can effectively integrate Copilot into the Weather Notification Service project and achieve high-quality results.
