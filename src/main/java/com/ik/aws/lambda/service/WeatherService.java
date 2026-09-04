package com.ik.aws.lambda.service;

import com.ik.aws.lambda.dto.WeatherForecast;
import com.ik.aws.lambda.dto.WeatherPredictionResponse;
import com.ik.aws.lambda.dto.WeatherResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.base-url:https://api.openweathermap.org/data/2.5}")
    private String baseUrl;

    private final RestClient restClient;

    public WeatherService() {
        this(RestClient.builder().build());
    }

    WeatherService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String fetchCurrentWeather(double lat, double lon) {
        log.info("Preparing current weather request for lat={}, lon={}", lat, lon);
        try {
            return buildLegacyCurrentWeatherUrl(lat, lon);
        } catch (Exception e) {
            log.error("Failed to prepare current weather request for lat={}, lon={}", lat, lon, e);
            throw e;
        }
    }

    public WeatherResponse fetchCurrentWeather(String location) {
        String normalizedLocation = normalizeLocation(location);
        log.info("Preparing current weather for location={}", normalizedLocation);

        Map<String, Object> response = fetchCurrentWeatherByCity(normalizedLocation);
        Map<String, Object> main = asMap(response.get("main"));
        double temperature = asDouble(main.get("temp"));
        String condition = firstWeatherMain(response);

        return new WeatherResponse(
                normalizedLocation,
                temperature,
                condition,
                String.format(Locale.ENGLISH, "%.0f°C and %s in %s.", temperature, condition.toLowerCase(Locale.ROOT), normalizedLocation)
        );
    }

    public String fetchWeatherPrediction(double lat, double lon) {
        log.info("Preparing weather prediction request for lat={}, lon={}", lat, lon);
        try {
            return buildLegacyPredictionUrl(lat, lon);
        } catch (Exception e) {
            log.error("Failed to prepare weather prediction request for lat={}, lon={}", lat, lon, e);
            throw e;
        }
    }

    public WeatherPredictionResponse fetchWeatherPrediction(String location) {
        String normalizedLocation = normalizeLocation(location);
        log.info("Preparing weather prediction for location={}", normalizedLocation);

        Map<String, Object> response = fetchForecastByCity(normalizedLocation);
        List<Map<String, Object>> list = asList(response.get("list"));
        List<WeatherForecast> predictions = new ArrayList<>();

        for (int i = 0; i < Math.min(5, list.size()); i++) {
            Map<String, Object> entry = list.get(i);
            long epochSeconds = asLong(entry.get("dt"));
            Map<String, Object> main = asMap(entry.get("main"));
            double temperature = asDouble(main.get("temp"));
            String condition = firstWeatherMain(entry);
            String time = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
            predictions.add(new WeatherForecast(time, temperature, condition));
        }

        return new WeatherPredictionResponse(normalizedLocation, predictions);
    }

    private Map<String, Object> fetchCurrentWeatherByCity(String location) {
        String url = buildCityWeatherUrl(location, true);
        Map<String, Object> body = restClient.get().uri(url).retrieve().body(Map.class);
        if (body == null || body.isEmpty()) {
            throw new IllegalStateException("No weather data returned for location=" + location);
        }
        return body;
    }

    private Map<String, Object> fetchForecastByCity(String location) {
        String url = buildCityForecastUrl(location, true);
        Map<String, Object> body = restClient.get().uri(url).retrieve().body(Map.class);
        if (body == null || body.isEmpty()) {
            throw new IllegalStateException("No forecast data returned for location=" + location);
        }
        return body;
    }

    private Coordinates resolveCoordinates(String location) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("weather.api.key is not configured");
        }

        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String geoUri = "https://api.openweathermap.org/geo/1.0/direct?q=" + encodedLocation + "&limit=1&appid=" + apiKey;
        List<Map<String, Object>> locations = asList(restClient.get().uri(geoUri).retrieve().body(List.class));

        if (locations == null || locations.isEmpty()) {
            throw new IllegalArgumentException("Could not resolve location: " + location);
        }

        Map<String, Object> city = locations.get(0);
        double lat = asDouble(city.get("lat"));
        double lon = asDouble(city.get("lon"));
        return new Coordinates(lat, lon);
    }

    private String buildLegacyCurrentWeatherUrl(double lat, double lon) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("weather.api.key is not configured");
        }
        String url = buildCoordinatesWeatherUrl(lat, lon, false);
        return url.replace("?lat=" + lat + "&lon=" + lon, "/current?lat=" + lat + "&lon=" + lon);
    }

    private String buildLegacyPredictionUrl(double lat, double lon) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("weather.api.key is not configured");
        }
        return buildCoordinatesWeatherUrl(lat, lon, false);
    }

    private String buildCityWeatherUrl(String location, boolean includeMetric) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("weather.api.key is not configured");
        }

        String endpoint = buildEndpointBase("weather");
        String encoded = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = endpoint + "?q=" + encoded;
        if (includeMetric) {
            url += "&units=metric";
        }
        url += "&appid=" + apiKey;
        return url;
    }

    private String buildCityForecastUrl(String location, boolean includeMetric) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("weather.api.key is not configured");
        }

        String endpoint = buildEndpointBase("forecast");
        String encoded = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = endpoint + "?q=" + encoded;
        if (includeMetric) {
            url += "&units=metric";
        }
        url += "&appid=" + apiKey;
        return url;
    }

    private String buildCoordinatesWeatherUrl(double lat, double lon, boolean includeMetric) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("weather.api.key is not configured");
        }

        String endpoint = buildEndpointBase("weather");
        String url = endpoint + "?lat=" + lat + "&lon=" + lon;
        if (includeMetric) {
            url += "&units=metric";
        }
        url += "&appid=" + apiKey;
        return url;
    }

    private String buildEndpointBase(String endpointName) {
        String endpoint = baseUrl == null || baseUrl.isBlank() ? "https://api.openweathermap.org/data/2.5" : baseUrl;
        endpoint = endpoint.replaceAll("/+$", "");
        if (endpoint.endsWith("/weather") || endpoint.endsWith("/forecast") || endpoint.endsWith("/onecall")) {
            return endpoint.replaceAll("/(weather|forecast|onecall)$", "") + "/" + endpointName;
        }
        if (endpoint.contains("/data/2.5")) {
            return endpoint + "/" + endpointName;
        }
        return endpoint + "/" + endpointName;
    }

    private String firstWeatherMain(Map<String, Object> payload) {
        List<Map<String, Object>> weather = asList(payload.get("weather"));
        if (weather == null || weather.isEmpty()) {
            return "Unknown";
        }
        Object main = weather.get(0).get("main");
        return main == null ? "Unknown" : String.valueOf(main);
    }

    private String normalizeLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("location must not be blank");
        }
        return location.trim();
    }

    private static Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private static List<Map<String, Object>> asList(Object value) {
        if (value instanceof List<?> list) {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof Map<?, ?> map) {
                    result.add((Map<String, Object>) map);
                }
            }
            return result;
        }
        return List.of();
    }

    private static double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return 0.0;
    }

    private static long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }

    private record Coordinates(double lat, double lon) {
    }
}
