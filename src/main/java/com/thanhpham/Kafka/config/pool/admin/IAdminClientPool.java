package com.thanhpham.Kafka.config.pool.admin;

import org.apache.kafka.clients.admin.Admin;

public interface IAdminClientPool {
    Admin get(String bootstrap);
}
