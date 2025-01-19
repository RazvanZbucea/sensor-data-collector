package com.example.sensor_data_collector.controller;

import com.example.sensor_data_collector.model.SensorMessage;
import com.example.sensor_data_collector.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SensorDataController {

    private final SensorService sensorService;

    public SensorDataController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/measurements")
    public ResponseEntity<String> receiveMeasurements(@RequestBody List<SensorMessage> messages) {
        sensorService.processSensorData(messages);
        return ResponseEntity.ok("Data received and processed.");
    }
}
