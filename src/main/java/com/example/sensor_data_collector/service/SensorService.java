package com.example.sensor_data_collector.service;

import com.example.sensor_data_collector.model.SensorAnalytics;
import com.example.sensor_data_collector.model.SensorMessage;
import com.example.sensor_data_collector.repository.SensorAnalyticsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SensorService {

    private static final String G_FORCE = "g-force";
    private final SensorAnalyticsRepository sensorAnalyticsRepository;
    private final Map<String, Deque<SensorMessage>> sensorDataMap;

    public SensorService(SensorAnalyticsRepository sensorAnalyticsRepository) {
        this.sensorAnalyticsRepository = sensorAnalyticsRepository;
        this.sensorDataMap = new ConcurrentHashMap<>();
    }

    public void processSensorData(List<SensorMessage> messages) {
        messages.forEach(message -> {
            // Normalize data to m/s² if needed
            if (G_FORCE.equals(message.getDataUnit())) {
                message.setData(convertToMetersPerSecondSquared(message.getData()));
            }

            // Add message to sliding window
            sensorDataMap.computeIfAbsent(message.getSensorId(), key -> new ArrayDeque<>()).add(message);

            // Maintain the sliding window
            maintainSlidingWindow(message.getSensorId());
        });
    }

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void computeAnalyticsWithShift() {
        sensorDataMap.forEach((sensorId, window) -> {
            // Maintain the sliding window before computing analytics
            maintainSlidingWindow(sensorId);

            // Compute and save analytics for the current sliding window
            computeAndSaveAnalytics(sensorId);
        });
    }

    private double[] convertToMetersPerSecondSquared(double[] data) {
        double gToMs2 = 9.80665; // Conversion factor
        return new double[]{data[0] * gToMs2, data[1] * gToMs2, data[2] * gToMs2};
    }

    private void maintainSlidingWindow(String sensorId) {
        Deque<SensorMessage> window = sensorDataMap.get(sensorId);
        long oneMinuteAgo = System.currentTimeMillis() - 60 * 1000;

        // Remove messages older than 1 minute
        while (!window.isEmpty() && parseTimestamp(window.peekFirst().getCreatedTime()) < oneMinuteAgo) {
            window.pollFirst();
        }
    }

    private long parseTimestamp(String timestamp) {
        return Instant.parse(timestamp).toEpochMilli();
    }

    private void computeAndSaveAnalytics(String sensorId) {
        Deque<SensorMessage> window = sensorDataMap.get(sensorId);

        if (!window.isEmpty()) {
            double[] sums = new double[3];
            int count = window.size();

            // Compute sums for x, y, z axes
            for (SensorMessage message : window) {
                for (int i = 0; i < message.getData().length; i++) {
                    sums[i] += message.getData()[i];
                }
            }

            // Calculate averages
            Map<String, Double> avg = Map.of(
                    "x", sums[0] / count,
                    "y", sums[1] / count,
                    "z", sums[2] / count
            );

            // Get the time range
            String start = window.getFirst().getCreatedTime();
            String end = window.getLast().getCreatedTime();

            // Save analytics to Elasticsearch
            SensorAnalytics analytics = new SensorAnalytics(sensorId + "_" + start, start, end, sensorId, avg);
            sensorAnalyticsRepository.save(analytics);
        }
    }
}
