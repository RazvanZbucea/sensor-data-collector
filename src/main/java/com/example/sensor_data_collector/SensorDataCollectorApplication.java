package com.example.sensor_data_collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SensorDataCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorDataCollectorApplication.class, args);
	}

}
