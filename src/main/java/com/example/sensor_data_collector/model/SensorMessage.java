package com.example.sensor_data_collector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorMessage {
    private String sensorId;
    private String createdTime;
    private double[] data; // x, y, z
    private String dataType;
    private String dataUnit;
}
