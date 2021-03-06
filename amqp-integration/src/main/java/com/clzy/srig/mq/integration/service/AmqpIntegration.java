package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.enums.MQStuats;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
        try {
            if (server.getRetry() != null && server.getRetry() < 0) {
                return;
            }
            log.debug("====amqp send msg：{}===", new String(message));
            Channel client = amqpService.getProducer(server);
//            client.queueDeclare(router.getToTopic(), false, false, false, null);
            client.basicPublish(server.getExchange(), router.getToTopic(), null, message);
            router.setStatus(MQStuats.online.getCode());
        } catch (Exception e) {
            log.error("AMQP消息推送失败");
            e.printStackTrace();
            router.setStatus(MQStuats.client_offline.getCode());
            if (server.getRetry() == null || server.getRetry() > 0) {
                server.setRetry(-1);
                amqpService.disProducer(server);
                try {
                    log.info("=====AMQP(RabbitMQ) producer重试连接=====");
                    Channel client = amqpService.getProducer(server);
//                    client.queueDeclare(router.getToTopic(), false, false, false, null);
                    client.basicPublish(server.getExchange(), router.getToTopic(), null, message);
                    router.setStatus(MQStuats.online.getCode());
                }catch (Exception e1){
                    router.setStatus(MQStuats.client_offline.getCode());
                    log.info("=====AMQP(RabbitMQ) producer重试连接失败=====");
                }
            }
        }
    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {
        for (ForwardRouter router : routers) {
            consumer(router);
        }
    }

    public void consumer(ForwardRouter router) {
        try {
            Channel client = amqpService.getConnect(router.getFromServer());
//            client.queueDeclare(router.getFromTopic(), false, false, false, null);
            String[] queues = router.getFromTopic().split(",");
            for (String queue : queues) {
                client.basicConsume(queue, (consumerTag, message) -> {
                    forwardService.publish(router.getFromServer(), message.getBody());
                }, consumerTag -> {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            router.setStatus(MQStuats.server_offline.getCode());
        }
    }

    @Override
    public void connect(ForwardRouter router) {
        amqpService.disConnect(router.getFromServer());
        consumer(router);
    }

    @Override
    public void disConnect(ForwardRouter router) {
        amqpService.disConnect(router.getFromServer());
        router.setStatus(MQStuats.offline.getCode());
    }

    @Override
    public boolean testConnect(ForwardRouter router) {
        try {
            amqpService.testConnect(router);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
