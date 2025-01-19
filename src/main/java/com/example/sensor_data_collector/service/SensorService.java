package com.example.sensor_data_collector.service;

import com.example.sensor_data_collector.model.SensorAnalytics;
import com.example.sensor_data_collector.model.SensorMessage;
import com.example.sensor_data_collector.repository.SensorAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service // Marks this class as a service
public class SensorService {

    private static final String G_FORCE = "g-force";

    private final SensorAnalyticsRepository sensorAnalyticsRepository;
    private final Map<String, Deque<SensorMessage>> sensorDataMap = new ConcurrentHashMap<>();

    public SensorService(SensorAnalyticsRepository sensorAnalyticsRepository) {
        this.sensorAnalyticsRepository = sensorAnalyticsRepository;
    }

    public void processSensorData(List<SensorMessage> messages) {
        messages.forEach(message -> {
            if (G_FORCE.equals(message.getDataUnit())) {
                message.setData(convertToMetersPerSecondSquared(message.getData()));
            }

            sensorDataMap.computeIfAbsent(message.getSensorId(), k -> new ArrayDeque<>()).add(message);

            maintainSlidingWindow(message.getSensorId());
            computeAndStoreAnalytics(message.getSensorId());
        });
    }

    private double[] convertToMetersPerSecondSquared(double[] data) {
        double gToMs2 = 9.80665; // Conversion factor
        return Arrays.stream(data).map(value -> value * gToMs2).toArray();
    }

    private long parseTimestamp(String timestamp) {
        return Instant.parse(timestamp).toEpochMilli();
    }

    private void maintainSlidingWindow(String sensorId) {
        Deque<SensorMessage> window = sensorDataMap.get(sensorId);
        long oneMinuteAgo = System.currentTimeMillis() - 60 * 1000;

        while (!window.isEmpty() && parseTimestamp(window.peekFirst().getCreatedTime()) < oneMinuteAgo) {
            window.pollFirst();
        }
    }

    private void computeAndStoreAnalytics(String sensorId) {
        Deque<SensorMessage> window = sensorDataMap.get(sensorId);

        if (!window.isEmpty()) {
            double[] sums = new double[3];
            int count = window.size();

            for (SensorMessage message : window) {
                for (int i = 0; i < message.getData().length; i++) {
                    sums[i] += message.getData()[i];
                }
            }

            Map<String, Double> avg = Map.of(
                    "x", sums[0] / count,
                    "y", sums[1] / count,
                    "z", sums[2] / count
            );

            String start = window.getFirst().getCreatedTime();
            String end = window.getLast().getCreatedTime();

            SensorAnalytics analytics = new SensorAnalytics(start, end, sensorId, avg);
            sensorAnalyticsRepository.save(analytics);
        }
    }
}
