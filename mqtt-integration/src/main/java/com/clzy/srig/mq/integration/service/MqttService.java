package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MqttService {

    private Map<String, MqttClient> mqttClientMap = new HashMap<>();

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
        MqttClient client = mqttClientMap.get(connectUrl);
        if (client == null) {
            client = new MqttClient(connectUrl, server.getClientName(), new MemoryPersistence());
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
        }
        return client;
    }

    public void testConncet(ForwardRouter router) throws Exception {
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
                client.close(true);
                client.disconnect();
                mqttClientMap.remove(connectUrl);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
