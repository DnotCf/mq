package com.clzy.srig.mq.integration.enums;

public interface MQIntegration {

    public enum ServerType {
        MQTT, AMQP, Aliyun_RocketMQ, RocketMQ, HTTP,Kafka
    }

    String defaultGroupName = "defalutGroup";
}
