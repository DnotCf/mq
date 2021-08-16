package com.clzy.sring.mq.service;

import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:52
 */
@Slf4j
@Service
public class RocketMqService {

    // 消费者线程数据量
    private Integer consumeThreadMin;
    private Integer consumeThreadMax;
    private Integer consumeMessageBatchMaxSize = 100;

    private Map<String, DefaultMQPushConsumer> consumerMap = new HashMap<>();
    private Map<String, DefaultMQProducer> producerMap = new HashMap<>();

    public DefaultMQPushConsumer createConsumer(MQServer server) throws MQClientException {
       
        String namesrvAddr = getConectionUrl(server);
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s:%d", server.getIp(), server.getPort());
        }
        if (consumerMap.get(namesrvAddr) != null) {
            log.info("====={}===activeMQ连接服务已经存在=====", namesrvAddr);
            return consumerMap.get(namesrvAddr);
        }
        log.info("====={}===activeMQ消费连接服务创建开始=====", namesrvAddr);
        JSONObject param = getDefaultParam(server.getDefaultParam());
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(param.getString("group"));
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumerMap.put(namesrvAddr, consumer);
        log.info("====={}===activeMQ消费连接服务创建成功=====", namesrvAddr);
        /**
         * 设置消费模型，集群还是广播，默认为集群
         */
//        consumer.setMessageModel(MessageModel.CLUSTERING);
//        consumer.setConsumeThreadMin(consumeThreadMin);
//        consumer.setConsumeThreadMax(consumeThreadMax);
        return consumer;
    }

    public DefaultMQProducer createProducer(MQServer server) throws MQClientException {
        String url = getConectionUrl(server);
        if (producerMap.get(url) != null) {
            return producerMap.get(url);
        }
        log.info("====={}===activeMQ生产服务连接创建开始=====", url);
        JSONObject param = getDefaultParam(server.getDefaultParam());
        DefaultMQProducer producer = new DefaultMQProducer(param.getString("group"));
        producer.setNamesrvAddr(url);
        producer.setVipChannelEnabled(false);
//        producer.setMaxMessageSize(maxMessageSize);
//        producer.setSendMsgTimeout(sendMsgTimeOut);
        producer.setRetryTimesWhenSendAsyncFailed(server.getRetry());
        producer.start();
        producerMap.put(url, producer);
        log.info("====={}===activeMQ生产服务连接服务创建成功=====", url);
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
        if (StringUtils.isBlank(jsonStr)) {
            JSONObject object = new JSONObject();
            object.put("group", MQIntegration.defaultGroupName);
            return object;
        }
        return JSONObject.parseObject(jsonStr);
    }

}
