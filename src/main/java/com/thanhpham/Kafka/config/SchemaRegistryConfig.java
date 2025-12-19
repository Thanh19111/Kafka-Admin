package com.thanhpham.Kafka.config;

import com.thanhpham.Kafka.util.Constants;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SchemaRegistryConfig {
    @Bean(destroyMethod = "close")
    SchemaRegistryClient initSchemaRegistryClient(){
        Map<String, Object> config = new HashMap<>();
        config.put("request.timeout.ms", 30000);
        config.put("retry.backoff.ms", 500);
        return new CachedSchemaRegistryClient(List.of(Constants.SCHEMA_REGISTRY_URL), 100, config);
    }
}
