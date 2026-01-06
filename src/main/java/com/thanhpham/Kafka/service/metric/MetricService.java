package com.thanhpham.Kafka.service.metric;

import lombok.RequiredArgsConstructor;
import org.jolokia.client.JolokiaClient;
import org.jolokia.client.exception.JolokiaException;
import org.jolokia.client.request.JolokiaReadRequest;
import org.jolokia.client.response.JolokiaReadResponse;
import org.springframework.stereotype.Service;

import javax.management.MalformedObjectNameException;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MetricService {
    private final JolokiaClient jolokiaClient;
    public void t() throws MalformedObjectNameException, JolokiaException {
        JolokiaReadRequest req = new JolokiaReadRequest("java.lang:type=Memory", "HeapMemoryUsage");
        JolokiaReadResponse resp = jolokiaClient.execute(req);
        Map<String, Long> vals = resp.getValue();
        long used = vals.get("used");
        long max = vals.get("max");
        int usage = (int) (used * 100 / max);
        System.out.println("Memory usage: used: " + used + " / max: " + max + " = " + usage + "%");
    }

}
