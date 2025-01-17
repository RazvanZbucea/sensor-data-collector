package com.example.sensor_data_collector.service;

import com.example.sensor_data_collector.model.SensorAnalytics;
import com.example.sensor_data_collector.repository.SensorAnalyticsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ElasticsearchTestService {

    private final SensorAnalyticsRepository sensorAnalyticsRepository;

    public ElasticsearchTestService(SensorAnalyticsRepository sensorAnalyticsRepository) {
        this.sensorAnalyticsRepository = sensorAnalyticsRepository;
    }

    @PostConstruct
    public void testElasticsearchConnection() {
        SensorAnalytics testResult = new SensorAnalytics(
                "id",
                "2025-01-17T10:00:00Z",
                "2025-01-17T10:01:00Z",
                "TestSensor",
                Map.of("x", 1.0, "y", 2.0, "z", 3.0)
        );

        sensorAnalyticsRepository.save(testResult);
    }
}

