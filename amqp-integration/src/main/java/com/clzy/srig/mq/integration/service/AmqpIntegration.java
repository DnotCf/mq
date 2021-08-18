package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component("amqpIntegration")
public class AmqpIntegration implements IMqIntegration {

    @Autowired
    private AmqpService amqpService;

    @Autowired
    private ForwardService forwardService;

    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.AMQP;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {
        MQServer server = router.getToServer();

        Channel client = amqpService.getConnect(server);

        try {
            client.queueDeclare(router.getToTopic(), false, false, false, null);
            client.basicPublish("", router.getToTopic(), null, message);
        } catch (Exception e) {
            log.error("AMQP消息推送失败");
            e.printStackTrace();
        }
    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {
        for (ForwardRouter router : routers) {
            Channel client = amqpService.getConnect(router.getFromServer());
            try {
                client.queueDeclare(router.getFromTopic(), false, false, false, null);
                client.basicConsume(router.getFromTopic(), (consumerTag, message) -> {
                    forwardService.publish(router.getFromServer(), message.getBody());
                }, consumerTag -> {

                });
            } catch (Exception e) {
                log.error("初始化AMQP连接失败", e);
            }
        }
    }

    @Override
    public void disConnect(ForwardRouter router) {
        amqpService.disConnect(router.getFromServer());
    }
}
