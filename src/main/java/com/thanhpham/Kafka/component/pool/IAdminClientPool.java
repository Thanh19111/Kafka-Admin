package com.thanhpham.Kafka.component.pool;

import org.apache.kafka.clients.admin.Admin;

public interface IAdminClientPool {
    Admin get(String bootstrap);
}
