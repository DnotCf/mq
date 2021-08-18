package com.clzy.sring.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardService;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:47
 */
@Slf4j
@Service
public class RocketAliyunMqIntegration implements IMqIntegration {

    @Autowired
    private RocketAliyunMqService aliyunMqService;
    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.Aliyun_RocketMQ;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {
        MQServer server = router.getToServer();
        try {
            ProducerBean producer = aliyunMqService.buildOrderProducer(router);
            String tag = server.getTag();
            if (StringUtils.isBlank(tag)) {
                tag = "*";
            }
            Message msg = new Message(router.getToTopic(), tag, message);
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
                ConsumerBean consumer = aliyunMqService.buildOrderConsumer(router);
                consumer.start();
            } catch (Exception e) {
                log.error("初始化RocketMq连接失败", e);
            }
        }
    }

    @Override
    public void disConnect(ForwardRouter router) {
        aliyunMqService.disConnect(router.getFromServer());
    }
}
