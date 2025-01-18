package com.example.sensor_data_collector.service;

import com.example.sensor_data_collector.model.SensorMessage;
import com.example.sensor_data_collector.repository.SensorAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SensorService {

    private static final String G_FORCE = "g-force";
    private final SensorAnalyticsRepository sensorAnalyticsRepository;

    public SensorService(SensorAnalyticsRepository sensorAnalyticsRepository) {
        this.sensorAnalyticsRepository = sensorAnalyticsRepository;
    }

    public void processSensorData(List<SensorMessage> messages) {
        messages.forEach(message -> {
            if (G_FORCE.equals(message.getDataUnit())) {
                message.setData(convertToMetersPerSecondSquared(message.getData()));
            }
            // Further processing (partitioning, analytics) here
        });
    }

    private double[] convertToMetersPerSecondSquared(double[] data) {
        double gToMs2 = 9.80665;
        return Arrays.stream(data).map(value -> value * gToMs2).toArray();
    }
}
