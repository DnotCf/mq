package com.clzy.srig.mq.integration.service;

import org.springframework.messaging.Message;

public class MqttReceive implements IMessageReceive {

    @Override
    public void onReceive(Message<?> message) {

        String clientId = message.getHeaders().get("clientId").toString();
        String topic = message.getHeaders().get("topic").toString();

    }
}
