package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.NodeResponse;
import org.apache.kafka.common.Node;

public class NodeMapper {
    public static NodeResponse toResponse(Node node){
        NodeResponse res = new NodeResponse();
        res.setId(node.id());
        res.setIdString(node.idString());
        res.setHost(node.host());
        res.setPort(node.port());
        res.setRack(node.rack());
        res.setFenced(node.isFenced());
        res.setEmpty(node.isEmpty());
        return res;
    }
}
