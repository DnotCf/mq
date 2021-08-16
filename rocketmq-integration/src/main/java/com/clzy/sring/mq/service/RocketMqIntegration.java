package com.clzy.sring.mq.service;

import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardService;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:47
 */
@Slf4j
@Service
public class RocketMqIntegration implements IMqIntegration {

    @Autowired
    private ForwardService forwardService;
    @Autowired
    private RocketMqService rocketMqService;
    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.RocketMQ;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {
        MQServer server = router.getToServer();
        try {
            DefaultMQProducer producer = rocketMqService.createProducer(server);
            Message msg = new Message(router.getToTopic(), message);
            producer.send(msg);
        } catch (Exception e) {
            log.error("RocketMq消息推送失败");
            e.printStackTrace();
        }
    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {
        for (ForwardRouter router : routers) {
            try {
                DefaultMQPushConsumer consumer = rocketMqService.createConsumer(router.getFromServer());
                if (consumer == null) {
                    continue;
                }
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

                String[] topicArr = router.getFromTopic().split(",");
                for (String tag : topicArr) {
                    if (StringUtils.isNotBlank(tag)) {
                        consumer.subscribe(router.getFromTopic(), "");
                    }
                }

                consumer.start();
            } catch (Exception e) {
                log.error("初始化RocketMq连接失败", e);
            }
        }
    }
}
