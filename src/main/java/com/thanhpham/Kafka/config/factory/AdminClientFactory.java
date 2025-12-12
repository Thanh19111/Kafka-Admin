package com.thanhpham.Kafka.config.factory;

import com.thanhpham.Kafka.utils.Constants;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AdminClientFactory {
    public Admin createAdmin(String bootstrap){
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, Constants.REQUEST_TIMEOUT_MS_CONFIG);
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, Constants.DEFAULT_API_TIMEOUT_MS_CONFIG);
        return AdminClient.create(props);
    }
}
