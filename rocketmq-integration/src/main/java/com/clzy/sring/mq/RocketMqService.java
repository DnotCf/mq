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
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
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
    @Autowired
    private ForwardService forwardService;

    private Integer consumeMessageBatchMaxSize = 100;

    private Map<String, DefaultMQPushConsumer> consumerMap = new HashMap<>();
    private Map<String, DefaultMQProducer> producerMap = new HashMap<>();

    public DefaultMQPushConsumer createConsumer(ForwardRouter router) throws MQClientException {
        MQServer server = router.getFromServer();
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
        consumer.subscribe(router.getFromTopic(), "*");
        consumerMap.put(namesrvAddr, consumer);
        log.info("====={}===activeMQ消费连接服务创建成功=====", namesrvAddr);
        return consumer;
    }

    public DefaultMQProducer createProducer(MQServer server) throws MQClientException {
        String url = getConectionUrl(server);
        if (producerMap.get(url) != null) {
            return producerMap.get(url);
        }
        log.info("====={}===activeMQ生产服务连接创建开始=====", url);
//        JSONObject param = getDefaultParam(server.getDefaultParam());
        String group = server.getGroup();
        if (StringUtils.isBlank(group)) {
            group = MQIntegration.defaultGroupName;
        }
        DefaultMQProducer producer = new DefaultMQProducer(group);
        producer.setNamesrvAddr(url);
        producer.setVipChannelEnabled(false);
//        producer.setMaxMessageSize(maxMessageSize);
//        producer.setSendMsgTimeout(sendMsgTimeOut);
        if (server.getRetry() != null) {
            producer.setRetryTimesWhenSendAsyncFailed(server.getRetry());
        }
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
        JSONObject object = null;
        if (StringUtils.isBlank(jsonStr)) {
            object = new JSONObject();
        }else {
            object = JSONObject.parseObject(jsonStr);
        }
        if (StringUtils.isBlank(object.getString("consumeMessageBatchMaxSize"))) {
            object.put("consumeMessageBatchMaxSize", consumeMessageBatchMaxSize);
        }

        return object;
    }

}
