package com.clzy.srig.mq.integration.service;

import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class AmqpService {

    private Map<String, Channel> amqpClientMap = new HashMap<>();

    public synchronized Channel getConnect(MQServer server) throws Exception {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        Channel client = amqpClientMap.get(connectUrl);
        if (client == null) {
            client = build(server);
            amqpClientMap.put(connectUrl, client);
        }
        return client;
    }

    public Channel getAmqpClientMap(MQServer server) {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        return amqpClientMap.get(connectUrl);
    }

    public Channel build(MQServer server) throws Exception {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        log.info("=====AMQP服务=RabbitMq连接开始=====");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(server.getIp());
        factory.setPort(server.getPort());
        factory.setUsername(server.getUsername());
        factory.setPassword(server.getPassword());
        factory.setVirtualHost(server.getClientName());
        Connection connection = factory.newConnection();
        Channel client =  connection.createChannel();
        log.info("=====AMQP服务=RabbitMq连接完成=====");
//                amqpClientMap.put(connectUrl, client);
        return client;
    }

    public void testConnect(ForwardRouter router) throws Exception {
        if (router.getToServer() != null) {
            Channel build = build(router.getToServer());
            build.close();
            return;
        }
        Channel client = build(router.getFromServer());
        if (StringUtils.isNotBlank(router.getFromTopic())) {
            client.queueDeclare(router.getFromTopic(), false, false, false, null);
            client.basicConsume(router.getFromTopic(), (consumerTag, message) -> {
//            forwardService.publish(router.getFromServer(), message.getBody());
            }, consumerTag -> {
            });
        }
        client.close();
    }

    public void disConnect(MQServer server) {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        Channel client = amqpClientMap.get(connectUrl);
        if (client != null) {
            try {
                client.close();
                amqpClientMap.remove(connectUrl);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

}
