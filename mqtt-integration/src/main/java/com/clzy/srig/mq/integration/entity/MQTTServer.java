package com.clzy.srig.mq.integration.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
public class MQTTServer extends MQServer {

    private String topic;

    private String clientId;
}
