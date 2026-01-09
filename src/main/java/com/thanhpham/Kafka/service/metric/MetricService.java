package com.thanhpham.Kafka.service.metric;

import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.dto.ui.BufferUI;
import com.thanhpham.Kafka.dto.ui.CpuUsageUI;
import com.thanhpham.Kafka.dto.ui.MessageCountUI;
import com.thanhpham.Kafka.dto.ui.UnderReplicaUI;
import lombok.RequiredArgsConstructor;
import org.jolokia.client.JolokiaClient;
import org.jolokia.client.exception.JolokiaException;
import org.jolokia.client.request.JolokiaReadRequest;
import org.jolokia.client.response.JolokiaReadResponse;
import org.springframework.stereotype.Service;

import javax.management.MalformedObjectNameException;
import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MetricService {
    private final JolokiaClient jolokiaClient;

    public Pair heap() throws MalformedObjectNameException, JolokiaException {
        try {
            JolokiaReadRequest req = new JolokiaReadRequest("java.lang:type=Memory", "HeapMemoryUsage");
            JolokiaReadResponse resp = jolokiaClient.execute(req);
            Map<String, Long> vals = resp.getValue();
            long used = vals.get("used");
            long max = vals.get("max");
            int usage = (int) (used * 100 / max);
            return new Pair(used +"", usage + "");
        } catch (Exception e) {
            return new Pair("0", "0");
        }
    }

    public CpuUsageUI cpu () throws MalformedObjectNameException, JolokiaException {
        try {
            JolokiaReadRequest req = new JolokiaReadRequest("java.lang:type=OperatingSystem", "ProcessCpuLoad", "SystemCpuLoad","AvailableProcessors");
            JolokiaReadResponse resp = jolokiaClient.execute(req);
            Map<String, BigDecimal> vals = resp.getValue();

            double systemCPU = vals.get("SystemCpuLoad").doubleValue();
            double kafkaCPU = vals.get("ProcessCpuLoad").doubleValue();

            systemCPU = Math.round(systemCPU * 100.0) / 100.0;

            kafkaCPU = Math.round(kafkaCPU * 100.0) / 100.0;

            return new CpuUsageUI(systemCPU, kafkaCPU);
        } catch (Exception e) {
            return new CpuUsageUI(0.0, 0.0);
        }
    }

    public UnderReplicaUI underReplica() throws JolokiaException, MalformedObjectNameException {
        try {
            JolokiaReadRequest req = new JolokiaReadRequest("kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions");
            JolokiaReadResponse resp = jolokiaClient.execute(req);
            Map<String, Long> vals = resp.getValue();

            JolokiaReadRequest req1 = new JolokiaReadRequest( "kafka.server:type=ReplicaManager,name=OfflineReplicaCount");
            JolokiaReadResponse resp1 = jolokiaClient.execute(req1);
            Map<String, Long> val1s = resp1.getValue();

            UnderReplicaUI res = new UnderReplicaUI();
            res.setUnderReplicaCount(Math.toIntExact(vals.get("Value")));
            res.setOfflineReplicaCount(Math.toIntExact(val1s.get("Value")));

            return res;
        } catch (Exception e) {
            return new UnderReplicaUI(0, 0);
        }
    }

    public MessageCountUI messageCount() throws MalformedObjectNameException, JolokiaException {
        try {
            JolokiaReadRequest messageInReq = new JolokiaReadRequest("kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec",
                    "Count");
            JolokiaReadResponse res = jolokiaClient.execute(messageInReq);
            Long messageInCount = res.getValue();

            JolokiaReadRequest byteOutReq = new JolokiaReadRequest("kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec",
                    "Count");
            JolokiaReadResponse res1 = jolokiaClient.execute(byteOutReq);
            Long byteOutCount = res1.getValue();

            MessageCountUI messageCountUI = new MessageCountUI();
            messageCountUI.setMessageInCount(messageInCount);
            messageCountUI.setByteOutCount(byteOutCount);
            return messageCountUI;
        } catch (Exception e) {
            return new MessageCountUI(0L, 0L);
        }
    }

    public BufferUI buffer() throws JolokiaException, MalformedObjectNameException {
        try {
            JolokiaReadRequest req = new JolokiaReadRequest("java.nio:type=BufferPool,name=direct", "TotalCapacity", "MemoryUsed");
            JolokiaReadResponse resp = jolokiaClient.execute(req);
            Map<String, Long> vals = resp.getValue();
            BufferUI buffer = new BufferUI();
            buffer.setMemoryUsed(vals.get("MemoryUsed"));
            buffer.setTotalCapacity(vals.get("TotalCapacity"));
            return buffer;
        } catch (Exception e) {
            return new BufferUI(0L, 0L);
        }
    }

    /// ///

    public Long getLongValueMetric(String metricName) throws JolokiaException, MalformedObjectNameException {
        try {
            JolokiaReadRequest messageInReq = new JolokiaReadRequest(metricName);
            JolokiaReadResponse res = jolokiaClient.execute(messageInReq);
            Map<String, Object> result = res.getValue();
            return (Long) result.get("Value");
        } catch (Exception e) {
            return 0L;
        }
    }

    public BigDecimal getBigDecimalValueMetric(String metricName) throws JolokiaException, MalformedObjectNameException {
        try {
            JolokiaReadRequest messageInReq = new JolokiaReadRequest(metricName);
            JolokiaReadResponse res = jolokiaClient.execute(messageInReq);
            Map<String, Object> result = res.getValue();

            return (BigDecimal) result.get("Value");
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }


    public void t(String query) throws JolokiaException, MalformedObjectNameException {
        JolokiaReadRequest messageInReq = new JolokiaReadRequest(query);
        JolokiaReadResponse res = jolokiaClient.execute(messageInReq);
        Map<String, Object> result = res.getValue();

        System.out.println(result);
    }

    public void t1(String query) throws JolokiaException, MalformedObjectNameException {
        System.out.println(jolokiaClient);
    }


}
