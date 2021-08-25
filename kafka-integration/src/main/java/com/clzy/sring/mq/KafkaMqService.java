package com.clzy.sring.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:52
 */
@Slf4j
@Service
public class KafkaMqService {
    @Autowired
    private ForwardService forwardService;

    private Integer consumeMessageBatchMaxSize = 100;

    private Map<String, Thread> consumerThread = new HashMap<>();
    private ConcurrentHashMap<String, KafkaConsumer> consumerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, KafkaProducer> producerMap = new ConcurrentHashMap<>();

    public synchronized KafkaConsumer createConsumer(ForwardRouter router) {
        MQServer server = router.getFromServer();
        String namesrvAddr = getConectionUrl(server);
        if (consumerMap.get(namesrvAddr) != null) {
            log.info("====={}===kafka连接服务已经存在=====", namesrvAddr);
            return consumerMap.get(namesrvAddr);
        }
        KafkaConsumer consumer = build(router);
        consumerMap.put(namesrvAddr, consumer);
        Thread thread = new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(500);
                if (!records.isEmpty()) {
                    Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
                    while (iterator.hasNext()) {
                        forwardService.publish(server, iterator.next().value().getBytes(StandardCharsets.UTF_8));
                    }
                }
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
        });
        thread.start();
        consumerThread.put(namesrvAddr, thread);
        return consumer;
    }

    public KafkaConsumer build(ForwardRouter router) {
        MQServer server = router.getFromServer();
        String namesrvAddr = getConectionUrl(server);
        log.info("====={}===kafka消费连接服务创建开始=====", namesrvAddr);
        JSONObject param = getDefaultParam(server.getDefaultParam());
        String group = server.getGroup();
        if (StringUtils.isBlank(group)) {
            group = MQIntegration.defaultGroupName;
        }
        Properties props = new Properties();
        props.put("bootstrap.servers", namesrvAddr);
        props.put("group.id", group);
        if (StringUtils.isNotBlank(param.getString("enable.auto.commit"))) {
            props.put("enable.auto.commit", param.getString("enable.auto.commit"));
        }
        if (StringUtils.isNotBlank(param.getString("max.poll.interval.ms"))) {
            props.put("max.poll.interval.ms", param.getString("max.poll.interval.ms"));
        }
        if (StringUtils.isNotBlank(param.getString("session.timeout.ms"))) {
            props.put("session.timeout.ms", param.getString("session.timeout.ms"));
        }
        if (StringUtils.isNotBlank(param.getString("auto.commit.interval.ms"))) {
            props.put("auto.commit.interval.ms", param.getString("auto.commit.interval.ms"));
        }
        if (StringUtils.isNotBlank(param.getString("max.poll.records"))) {
            props.put("max.poll.records", param.getString("max.poll.records"));
        }
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("auto.offset.reset", "latest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(server.getTopic()));
//        consumerMap.put(namesrvAddr, consumer);
        log.info("====={}===kafka消费连接服务创建成功=====", namesrvAddr);
        return consumer;
    }

    public void testConnect(ForwardRouter router) throws Exception {
        if (router.getToServer() != null) {
            KafkaProducer producer = buildProducer(router.getToServer());
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(router.getToServer().getTopic(), "testConnect");
            producer.send(record);
            producer.close();
            return;
        }
        if (StringUtils.isBlank(router.getFromTopic())) {
            router.setFromTopic("testConnect");
        }
        KafkaConsumer consumer = build(router);
        ConsumerRecords<String, String> records = consumer.poll(500);
        consumer.close();
    }

    public synchronized KafkaProducer createProducer(MQServer server){
        String url = getConectionUrl(server);
        if (producerMap.get(url) != null) {
            return producerMap.get(url);
        }
        KafkaProducer producer = buildProducer(server);
        producerMap.put(url, producer);
        return producer;
    }

    public KafkaProducer buildProducer(MQServer server){
        String url = getConectionUrl(server);
        log.info("====={}===kafka生产服务连接创建开始=====", url);
//        JSONObject param = getDefaultParam(server.getDefaultParam());
        String group = server.getGroup();
        if (StringUtils.isBlank(group)) {
            group = MQIntegration.defaultGroupName;
        }
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
//        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,"0");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        KafkaProducer producer = new KafkaProducer<>(properties);
        log.info("====={}===kafka生产服务连接服务创建成功=====", url);
        return producer;
    }

    public String getConectionUrl(MQServer server) {
        String namesrvAddr = server.getCluster();
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s:%d", server.getIp(), server.getPort());
        }
        return namesrvAddr;
    }

    public JSONObject getDefaultParam(String jsonStr) {
        JSONObject object = null;
        if (StringUtils.isBlank(jsonStr)) {
            object = new JSONObject();
        } else {
            object = JSONObject.parseObject(jsonStr);
        }
        if (StringUtils.isBlank(object.getString("consumeMessageBatchMaxSize"))) {
            object.put("consumeMessageBatchMaxSize", consumeMessageBatchMaxSize);
        }

        return object;
    }

    public synchronized void disConnect(MQServer server) {
        String namesrvAddr = getConectionUrl(server);
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s:%d", server.getIp(), server.getPort());
        }
        KafkaConsumer consumer = consumerMap.get(namesrvAddr);
        if (consumer != null) {
            consumerMap.remove(namesrvAddr);
            Thread thread = consumerThread.get(namesrvAddr);
            if (thread != null) {
                thread.interrupt();
                consumerThread.remove(namesrvAddr);
                thread.stop();
            }
            consumer.unsubscribe();
            consumer.close();
        }
    }

    public synchronized void disConnectProducer(MQServer server) {
        String namesrvAddr = getConectionUrl(server);
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s:%d", server.getIp(), server.getPort());
        }
        KafkaProducer producer = producerMap.get(namesrvAddr);
        if (producer != null) {
            producerMap.remove(namesrvAddr);
            producer.close();
        }
    }

}
