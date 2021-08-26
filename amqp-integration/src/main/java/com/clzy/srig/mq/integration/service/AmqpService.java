package com.clzy.srig.mq.integration.service;

import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.rabbitmq.client.BuiltinExchangeType;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class AmqpService {

    private ConcurrentHashMap<String, Channel> amqpClientMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Channel> producertMap = new ConcurrentHashMap<>();

    public synchronized Channel getConnect(MQServer server) throws Exception {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        Channel client = amqpClientMap.get(connectUrl);
        if (client == null) {
            client = build(server);
            amqpClientMap.put(connectUrl, client);
        }
        return client;
    }

    public synchronized Channel getProducer(MQServer server) throws Exception {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        Channel client = producertMap.get(connectUrl);
        if (client == null) {
            client = build(server);
            JSONObject defaultParam = getDefaultParam(server.getDefaultParam());
            String exchange=defaultParam.getString("exchange");
            String type = defaultParam.getString("type");
            if (!StringUtils.isBlank(exchange)) {
                if (StringUtils.isBlank(type)) {
                    type = BuiltinExchangeType.DIRECT.getType();
                }
                server.setExchange(exchange);
                client.exchangeDeclare(exchange, type);
            }
            String queueName = defaultParam.getString("queue");
            if (StringUtils.isBlank(queueName)) {
                queueName = server.getTopic();
            }
            client.queueDeclare(queueName, false, false, false, null);
            if (StringUtils.isNotBlank(exchange)) {
                client.exchangeBind(queueName, exchange, server.getTopic());
            }
            producertMap.put(connectUrl, client);
        }
        return client;
    }

    public Channel getAmqpClientMap(MQServer server) {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        return amqpClientMap.get(connectUrl);
    }

    public Channel build(MQServer server) throws Exception {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        log.info("={}====AMQP服务=RabbitMq连接开始=====",connectUrl);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(server.getIp());
        factory.setPort(server.getPort());
        factory.setUsername(server.getUsername());
        factory.setPassword(server.getPassword());
        factory.setVirtualHost(server.getClientName());
        Connection connection = factory.newConnection();
        Channel client = connection.createChannel();
        log.info("=={}===AMQP服务=RabbitMq连接完成=====",connectUrl);
//                amqpClientMap.put(connectUrl, client);
        return client;
    }

    public JSONObject getDefaultParam(String jsonStr) {
        JSONObject object = null;
        if (StringUtils.isBlank(jsonStr)) {
            object = new JSONObject();
        } else {
            object = JSONObject.parseObject(jsonStr);
        }
        if (StringUtils.isBlank(object.getString("exchange"))) {
            object.put("exchange", "");
        }
        return object;
    }

    public void testConnect(ForwardRouter router) throws Exception {
        if (router.getToServer() != null) {
            MQServer server = router.getToServer();
            Channel client = build(server);
            JSONObject defaultParam = getDefaultParam(server.getDefaultParam());
            String exchange=defaultParam.getString("exchange");
            String type = defaultParam.getString("type");
            if (!StringUtils.isBlank(exchange)) {
                if (StringUtils.isBlank(type)) {
                    type = BuiltinExchangeType.DIRECT.getType();
                }
                server.setExchange(exchange);
                client.exchangeDeclare(exchange, type);
            }
            String queueName = defaultParam.getString("queue");
            if (StringUtils.isBlank(queueName)) {
                queueName = server.getTopic();
            }
            client.queueDeclare(queueName, false, false, false, null);
            if (StringUtils.isNotBlank(exchange)) {
                client.exchangeBind(queueName, exchange, server.getTopic());
            }
            client.close();
            return;
        }
        Channel client = build(router.getFromServer());
        if (StringUtils.isNotBlank(router.getFromTopic())) {
//            client.queueDeclare(router.getFromTopic(), false, false, false, null);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            amqpClientMap.remove(connectUrl);
        }
    }
    public void disProducer(MQServer server) {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        Channel client = producertMap.get(connectUrl);
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            producertMap.remove(connectUrl);
        }
    }

}
