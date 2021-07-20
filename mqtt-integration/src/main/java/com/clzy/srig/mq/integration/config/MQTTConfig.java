package com.clzy.srig.mq.integration.config;

import com.clzy.srig.mq.integration.entity.MQTTServer;
import com.clzy.srig.mq.integration.service.IMessageReceive;
import com.clzy.srig.mq.integration.service.MqttGateway;
import com.clzy.srig.mq.integration.service.MqttReceive;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@IntegrationComponentScan
public class MQTTConfig {

    private IMessageReceive receive = new MqttReceive();

    @Bean
    @ConfigurationProperties(prefix = "spring.mqtt.from")
    public MQTTServer fromMqttServer() {
        return new MQTTServer();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.mqtt.to")
    public MQTTServer toMqttServer() {
        return new MQTTServer();
    }

    @Bean
    public MqttConnectOptions fromMqttConnectOptions(@Qualifier("fromMqttServer") MQTTServer mqttServer) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        if (StringUtils.isNotBlank(mqttServer.getUsername())) {
            mqttConnectOptions.setUserName(mqttServer.getUsername());
        }
        if (StringUtils.isNotBlank(mqttServer.getPassword())) {
            mqttConnectOptions.setPassword(mqttServer.getPassword().toCharArray());
        }

        mqttConnectOptions.setServerURIs(new String[]{String.format("%s://%s:%d", mqttServer.getProtocol(),
                mqttServer.getHost(), mqttServer.getPort())});
        mqttConnectOptions.setKeepAliveInterval(2);
        return mqttConnectOptions;
    }

    @Bean
    public MqttConnectOptions toMqttConnectOptions(@Qualifier("toMqttServer") MQTTServer mqttServer) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        if (StringUtils.isNotBlank(mqttServer.getUsername())) {
            mqttConnectOptions.setUserName(mqttServer.getUsername());
        }
        if (StringUtils.isNotBlank(mqttServer.getPassword())) {
            mqttConnectOptions.setPassword(mqttServer.getPassword().toCharArray());
        }
        mqttConnectOptions.setServerURIs(new String[]{String.format("%s://%s:%d", mqttServer.getProtocol(),
                mqttServer.getHost(), mqttServer.getPort())});
        mqttConnectOptions.setKeepAliveInterval(2);
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory fromMqttClientFactory(@Qualifier("fromMqttConnectOptions") MqttConnectOptions mqttConnectOptions) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

    @Bean
    public MqttPahoClientFactory toMqttClientFactory(@Qualifier("toMqttConnectOptions") MqttConnectOptions mqttConnectOptions) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

    //接收通道
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    //配置client,监听的topic
    @Bean
    public MessageProducer inbound(@Qualifier("fromMqttServer") MQTTServer mqttServer, @Qualifier("fromMqttClientFactory") MqttPahoClientFactory mqttPahoClientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttServer.getClientId(), mqttPahoClientFactory,
                        mqttServer.getTopic().split(","));
        //adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    //通过通道获取数据
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(MqttGateway mqttGateway) {
        return message -> {
            String payload = message.getPayload().toString();
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            mqttGateway.sendToMqtt(topic, payload);
        };
    }


    /**
     * @author huangquanguang
     * @date 2020/1/3 16:37
     * @description 发送消息
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /*****
     * 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outbound(@Qualifier("toMqttServer") MQTTServer mqttServer, @Qualifier("toMqttClientFactory") MqttPahoClientFactory mqttPahoClientFactory) {
        // 在这里进行mqttOutboundChannel的相关设置
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttServer.getClientId(), mqttPahoClientFactory);
        messageHandler.setAsync(true); //如果设置成true，发送消息时将不会阻塞。
        // messageHandler.setDefaultTopic(topic);
        return messageHandler;
    }
}
