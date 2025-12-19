package com.thanhpham.Kafka.config;

import com.thanhpham.Kafka.config.property.SchemaRegistryProperties;
import com.thanhpham.Kafka.util.Constants;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SchemaRegistryConfig {
    private final SchemaRegistryProperties properties;

    @Bean(destroyMethod = "close")
    SchemaRegistryClient initSchemaRegistryClient(){
        Map<String, Object> config = new HashMap<>();
        config.put("request.timeout.ms", 30000);
        config.put("retry.backoff.ms", 500);
        return new CachedSchemaRegistryClient(List.of(properties.getUrl()), 100, config);
    }
}
