//package com.thanhpham.Kafka.config;
//
//import com.thanhpham.Kafka.utils.Constants;
//import org.apache.kafka.clients.admin.AdminClient;
//import org.apache.kafka.clients.admin.AdminClientConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Properties;
//
//@Configuration
//public class KafkaAdminConfig {
//    @Bean
//    public AdminClient createAdminClient(String serverUrl){
//        Properties props = new Properties();
//        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverUrl);
//        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, Constants.REQUEST_TIMEOUT_MS_CONFIG);
//        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, Constants.DEFAULT_API_TIMEOUT_MS_CONFIG);
//        return AdminClient.create(props);
//    }
//}
