package com.clzy.sring.mq;

import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:52
 */
@Slf4j
@Service
public class RocketMqService {
    @Autowired
    private ForwardService forwardService;

    private Integer consumeMessageBatchMaxSize = 100;

    private ConcurrentHashMap<String, DefaultMQPushConsumer> consumerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, DefaultMQProducer> producerMap = new ConcurrentHashMap<>();

    public synchronized DefaultMQPushConsumer createConsumer(ForwardRouter router) throws MQClientException {
        MQServer server = router.getFromServer();
        String namesrvAddr = getConectionUrl(server);
        if (consumerMap.get(namesrvAddr) != null) {
            log.info("====={}===activeMQ连接服务已经存在=====", namesrvAddr);
            return consumerMap.get(namesrvAddr);
        }
        DefaultMQPushConsumer consumer = build(router);
        consumer.start();
        consumerMap.put(namesrvAddr, consumer);
        return consumer;
    }

    public DefaultMQPushConsumer build(ForwardRouter router) throws MQClientException {
        MQServer server = router.getFromServer();
        String namesrvAddr = getConectionUrl(server);
        log.info("====={}===activeMQ消费连接服务创建开始=====", namesrvAddr);
        JSONObject param = getDefaultParam(server.getDefaultParam());
        String group = server.getGroup();
        if (StringUtils.isBlank(group)) {
            group = MQIntegration.defaultGroupName;
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);

        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeMessageBatchMaxSize(param.getInteger("consumeMessageBatchMaxSize"));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        // 设置监听
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            if (CollectionUtils.isEmpty(list)) {
                log.debug("rocketMQ接收消息为空，直接返回成功");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            for (MessageExt msg : list) {
                forwardService.publish(router.getFromServer(), msg.getBody());
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        String tag = server.getTag();
        if (StringUtils.isBlank(tag)) {
            tag = "*";
        }
        if (router.getFromTopic().contains(",")) {
            String[] topic = router.getFromTopic().split(",");
            String[] tg = new String[topic.length];
            if (tag.contains(",")) {
                tg = tag.split(",");
            }
            for (int i = 0; i < topic.length; i++) {
                if (i < tg.length && StringUtils.isNotBlank(tg[i])) {
                    consumer.subscribe(topic[i], tg[i]);
                }else {
                    consumer.subscribe(topic[i], "*");
                }
            }
        }else {
            consumer.subscribe(router.getFromTopic(), tag);
        }
//        consumerMap.put(namesrvAddr, consumer);
        log.info("====={}===activeMQ消费连接服务创建成功=====", namesrvAddr);
        return consumer;
    }

    public void testConnect(ForwardRouter router) throws Exception {
        if (router.getToServer() != null) {
            DefaultMQProducer producer = buildProducer(router.getToServer());
            Message msg = new Message(router.getToTopic(), "TestConnectMessage".getBytes(StandardCharsets.UTF_8));
            producer.send(msg);
            producer.shutdown();
            return;
        }
        if (StringUtils.isBlank(router.getFromTopic())) {
            router.setFromTopic("testConnect");
        }
        DefaultMQPushConsumer consumer = build(router);
        consumer.start();
        consumer.shutdown();
    }

    public synchronized DefaultMQProducer createProducer(MQServer server) throws MQClientException {
        String url = getConectionUrl(server);
        if (producerMap.get(url) != null) {
            return producerMap.get(url);
        }
        DefaultMQProducer producer = buildProducer(server);
        producerMap.put(url, producer);
        return producer;
    }

    public DefaultMQProducer buildProducer(MQServer server) throws MQClientException {
        String url = getConectionUrl(server);
        log.info("====={}===activeMQ生产服务连接创建开始=====", url);
        JSONObject param = getDefaultParam(server.getDefaultParam());
        String group = server.getGroup();
        if (StringUtils.isBlank(group)) {
            group = MQIntegration.defaultGroupName;
        }
        DefaultMQProducer producer = new DefaultMQProducer(group);
        producer.setNamesrvAddr(url);
        
        producer.setVipChannelEnabled(param.getBoolean("VipChannelEnabled"));
//        producer.
//        producer.setMaxMessageSize(maxMessageSize);
        producer.setSendMsgTimeout(param.getInteger("sendMsgTimeOut"));
        if (server.getRetry() != null) {
            producer.setRetryTimesWhenSendFailed(server.getRetry());
            producer.setRetryTimesWhenSendAsyncFailed(server.getRetry());
        }
        producer.start();
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
        JSONObject object = null;
        if (StringUtils.isBlank(jsonStr)) {
            object = new JSONObject();
        } else {
            object = JSONObject.parseObject(jsonStr);
        }
        if (StringUtils.isBlank(object.getString("consumeMessageBatchMaxSize"))) {
            object.put("consumeMessageBatchMaxSize", consumeMessageBatchMaxSize);
        }
        if (object.getInteger("sendMsgTimeOut") == null) {
            object.put("sendMsgTimeOut", 150000);
        }
        if (object.getBoolean("VipChannelEnabled") == null) {
            object.put("VipChannelEnabled", false);
        }
        return object;
    }

    public synchronized void disConnect(MQServer server) {
        String namesrvAddr = getConectionUrl(server);
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s:%d", server.getIp(), server.getPort());
        }
        DefaultMQPushConsumer consumer = consumerMap.get(namesrvAddr);
        if (consumer != null) {
            consumerMap.remove(namesrvAddr);
            consumer.shutdown();
        }
    }

    public synchronized void disConnectProducer(MQServer server) {
        String namesrvAddr = getConectionUrl(server);
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s:%d", server.getIp(), server.getPort());
        }
        DefaultMQProducer producer = producerMap.get(namesrvAddr);
        if (producer != null) {
            producerMap.remove(namesrvAddr);
            producer.shutdown();
        }
    }

}
