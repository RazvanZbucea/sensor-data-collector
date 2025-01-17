package com.example.sensor_data_collector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "sensor-analytics")
public class SensorAnalytics {
    @Id
    private String id;
    private String start;
    private String end;
    private String sensorId;
    private Map<String, Double> avg; //{"x": val, "y": val, "z": val}
}
