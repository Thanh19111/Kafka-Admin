//package com.thanhpham.Kafka.components;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JsonListener {
//    @KafkaListener(
//            topics = "test",
//            containerFactory = "jsonKafkaFactory"
//    )
//    public void listen(JsonNode node) {
//        System.out.println(">>Json Message: " + node);
//    }
//}
