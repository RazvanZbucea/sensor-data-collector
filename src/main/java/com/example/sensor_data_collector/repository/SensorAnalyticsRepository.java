package com.example.sensor_data_collector.repository;

import com.example.sensor_data_collector.model.SensorAnalytics;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorAnalyticsRepository extends ElasticsearchRepository<SensorAnalytics, String> {
}
