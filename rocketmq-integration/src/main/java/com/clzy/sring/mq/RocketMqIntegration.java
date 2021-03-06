package com.clzy.sring.mq;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.enums.MQStuats;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:47
 */
@Slf4j
@Service
public class RocketMqIntegration implements IMqIntegration {


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
            if (server.getRetry() != null && server.getRetry() < 0) {
                return;
            }
            log.debug("===={} send msg：{}===", type(), new String(message));
            DefaultMQProducer producer = rocketMqService.createProducer(server);
            String[] topics = server.getTopic().split(",");
            String tag = StringUtils.isBlank(server.getTag()) ? "*" : server.getTag();
            String[] tags = tag.split(",");
            for (int i = 0; i < topics.length; i++) {
                Message msg = null;
                if (i < tags.length) {
                    msg = new Message(topics[i], tags[i], message);
                } else {
                    msg = new Message(topics[i], message);
                }
                producer.send(msg);
            }
//            for (String topic : topics) {
//                Message msg = null;
//                if (StringUtils.isBlank(server.getTag())) {
//                    msg=new Message(router.getToTopic(), message);
//                }else {
//                    msg = new Message(topic, server.getTag(), message);
//                }
//                producer.send(msg);
//            }
            router.setStatus(MQStuats.online.getCode());
        } catch (Exception e) {
            log.error("RocketMq消息推送失败");
            router.setStatus(MQStuats.client_offline.getCode());
            e.printStackTrace();
            if (server.getRetry() == null || server.getRetry() > 0) {
                server.setRetry(-1);
                rocketMqService.disConnectProducer(server);
                try {
                    log.info("=====RocketMq producer重试连接=====");
                    DefaultMQProducer producer = rocketMqService.createProducer(server);
                    String[] topics = server.getTopic().split(",");
                    String tag = StringUtils.isBlank(server.getTag()) ? "*" : server.getTag();
                    String[] tags = tag.split(",");
                    for (int i = 0; i < topics.length; i++) {
                        Message msg = null;
                        if (i < tags.length) {
                            msg = new Message(topics[i], tags[i], message);
                        } else {
                            msg = new Message(topics[i], message);
                        }
                        producer.send(msg);
                    }
                    router.setStatus(MQStuats.online.getCode());
                } catch (Exception e1) {
                    router.setStatus(MQStuats.client_offline.getCode());
                    log.error("=====RocketMq producer重试连接失败=====");
                }

            }
        }
    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {
        for (ForwardRouter router : routers) {
            connect(router);
        }
    }

    @Override
    public void connect(ForwardRouter router) {
        try {
            disConnect(router);
            rocketMqService.createConsumer(router);
        } catch (Exception e) {
            router.setStatus(MQStuats.server_offline.getCode());
            log.error("初始化RocketMq连接失败", e);
        }
    }

    @Override
    public void disConnect(ForwardRouter router) {
        rocketMqService.disConnect(router.getFromServer());
        router.setStatus(MQStuats.offline.getCode());
    }

    @Override
    public boolean testConnect(ForwardRouter router) {
        try {
            rocketMqService.testConnect(router);
            return true;
        } catch (Exception e) {
            if (e instanceof MQClientException) {
                return true;
            }
            e.printStackTrace();
            return false;
        }
    }
}
