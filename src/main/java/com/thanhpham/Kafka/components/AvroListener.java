//package com.thanhpham.Kafka.components;
//
//import org.apache.avro.generic.GenericRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AvroListener {
//    @KafkaListener(
//            topics = "avro",
//            containerFactory = "avroKafkaFactory"
//    )
//    public void listen(GenericRecord record) {
//        System.out.println(">>Avro Message: " + record);
//    }
//}
