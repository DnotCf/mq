package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.MQServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MqttService {

    private Map<String, MqttClient> mqttClientMap = new HashMap<>();

    public synchronized MqttClient getClient(MQServer server) {

        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        MqttClient client = mqttClientMap.get(connectUrl);
        if (client == null) {
            try {
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
                mqttClientMap.put(connectUrl, client);
            } catch (Exception e) {
                log.error("MQTT连接异常", e);
                client = null;
            }

        }

        return client;
    }

    public void disConnect(MQServer server) {
        String connectUrl = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        MqttClient client = mqttClientMap.get(connectUrl);
        if (client != null) {
            try {
                client.disconnect();
                mqttClientMap.remove(connectUrl);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
