package com.clzy.srig.mq.integration.service;

import org.springframework.messaging.Message;

public interface IMessageReceive {

    void onReceive(Message<?> message);
}
