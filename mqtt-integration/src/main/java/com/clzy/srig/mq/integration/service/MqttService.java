package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQStuats;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MqttService {

    private ConcurrentHashMap<String, MqttClient> mqttClientMap = new ConcurrentHashMap<>();

    public synchronized MqttClient getClient(MQServer server) throws MqttException {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        MqttClient client = mqttClientMap.get(connectUrl);
        if (client == null) {
            client = build(server);
            mqttClientMap.put(connectUrl, client);
        }
        return client;
    }

    public MqttClient getMqttClientMap(MQServer server) {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        return mqttClientMap.get(connectUrl);
    }

    public MqttClient build(MQServer server) throws MqttException {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        log.info("=={}===MQTT 连接开始=====",connectUrl);
        MqttClient client = new MqttClient(connectUrl, server.getClientName(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        if (StringUtils.isNotBlank(server.getUsername())) {
            options.setUserName(server.getUsername());
        }
        if (StringUtils.isNotBlank(server.getUsername())) {
            options.setPassword(server.getPassword().toCharArray());
        }
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        client.connect(options);
        log.info("==={}==MQTT 连接完成=====", connectUrl);
        return client;
    }

    public void testConncet(ForwardRouter router) throws Exception {
        if (router.getToServer() != null) {
            MqttClient build = build(router.getToServer());
  /*          build.subscribe(router.getToTopic(),1);*/
            build.publish(router.getToTopic(), "testMsg".getBytes(StandardCharsets.UTF_8), 1, false);
            build.disconnect();
            build.close(true);
            return;
        }
        MqttClient client = build(router.getFromServer());
        if (StringUtils.isNotBlank(router.getFromTopic())) {
            client.subscribe(router.getFromTopic().split(","));
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //todo 重新连接
                    log.error("=====断开连接=====", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    forwardService.publish(router.getFromServer(), message.getPayload());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }
        client.disconnect();
        client.close(true);
    }

    public void disConnect(MQServer server) {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        MqttClient client = mqttClientMap.get(connectUrl);
        if (client != null) {
            try {
                client.disconnect();
                client.close(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mqttClientMap.remove(connectUrl);
        }
    }

}
