package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.MQServer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AmqpService {

    private Map<String, Channel> amqpClientMap = new HashMap<>();

    public synchronized Channel getConnect(MQServer server) {

        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        Channel client = amqpClientMap.get(connectUrl);
        if (client == null) {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(server.getIp());
                factory.setPort(server.getPort());
                factory.setUsername(server.getUsername());
                factory.setPassword(server.getPassword());
                factory.setVirtualHost(server.getClientName());

                Connection connection = factory.newConnection();
                client = connection.createChannel();
            } catch (Exception e) {
                log.error("AMQP连接异常", e);
                client = null;
            }

        }
        return client;
    }
}
