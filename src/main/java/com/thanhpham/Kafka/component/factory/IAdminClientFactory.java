package com.thanhpham.Kafka.component.factory;

import org.apache.kafka.clients.admin.Admin;

public interface IAdminClientFactory {
    Admin createAdmin(String bootstrap);
}
