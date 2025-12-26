//package com.thanhpham.Kafka.config;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
//import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializerConfig;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Configuration
//public class JsonConsumerConfig {
//    @Bean
//    public Consumer<String, JsonNode> jsonConsumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID());
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonSchemaDeserializer.class);
//        props.put(KafkaJsonSchemaDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
//        props.put(KafkaJsonSchemaDeserializerConfig.JSON_VALUE_TYPE, JsonNode.class);
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//
//     cho spring-kafka dùng để test - sẽ xóa
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, JsonNode> jsonKafkaFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, JsonNode> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(jsonConsumerFactory());
//        return factory;
//    }
//}
