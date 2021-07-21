package com.clzy.srig.mq.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQTTServer extends MQServer {

    private String topic;

    private String clientId;
}
