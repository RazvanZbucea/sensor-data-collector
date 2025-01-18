package com.example.sensor_data_collector.scheduler;

import com.example.sensor_data_collector.model.SensorAnalytics;
import com.example.sensor_data_collector.model.SensorMessage;
import com.example.sensor_data_collector.repository.SensorAnalyticsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnalyticsScheduler {

    private final Map<String, List<SensorMessage>> sensorDataMap = new ConcurrentHashMap<>();
    private final SensorAnalyticsRepository sensorAnalyticsRepository;

    public AnalyticsScheduler(SensorAnalyticsRepository sensorAnalyticsRepository) {
        this.sensorAnalyticsRepository = sensorAnalyticsRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void computeAnalytics() {
        sensorDataMap.forEach((sensorId, messages) -> {
            Map<String, Double> avg = computeAverage(messages);
            String start = messages.get(0).getCreatedTime();
            String end = messages.get(messages.size() - 1).getCreatedTime();

            SensorAnalytics result = new SensorAnalytics(start, end, sensorId, avg);
            sensorAnalyticsRepository.save(result);
        });
    }

    private Map<String, Double> computeAverage(List<SensorMessage> messages) {
        double[] sums = new double[3];
        int count = messages.size();

        messages.forEach(msg -> {
            for (int i = 0; i < msg.getData().length; i++) {
                sums[i] += msg.getData()[i];
            }
        });

        return Map.of("x", sums[0] / count, "y", sums[1] / count, "z", sums[2] / count);
    }
}
