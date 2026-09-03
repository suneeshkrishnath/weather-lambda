package com.ik.aws.lambda.service;

import com.ik.aws.lambda.dto.WeatherSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherSnapshotService {

    public WeatherSnapshot currentSnapshot(double lat, double lon) {
        log.info("Generating weather snapshot for lat={}, lon={}", lat, lon);
        try {
            double temperature = 18.0 + (lat % 10) - (lon % 5);
            double precipitation = Math.abs((lat + lon) % 3) * 0.5;
            String condition = precipitation > 0.75 ? "Rain" : temperature > 24 ? "Sunny" : "Cloudy";
            return new WeatherSnapshot(temperature, precipitation, condition);
        } catch (Exception e) {
            log.error("Failed to generate weather snapshot for lat={}, lon={}", lat, lon, e);
            throw e;
        }
    }
}
