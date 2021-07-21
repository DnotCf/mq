package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("mqttIntegration")
public class MqttIntegration implements IMqIntegration {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private ForwardService forwardService;

    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.MQTT;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {
        MQServer server = router.getToServer();

        MqttClient client = mqttService.getClient(server);

        try {
            client.publish(router.getToTopic(), message, 1, false);
        } catch (MqttException e) {
            log.error("MQTT消息推送失败");
            e.printStackTrace();
        }
    }

    @Override
    public void initReceiver(List<ForwardRouter> forwardRouters) {
        for (ForwardRouter router : forwardRouters) {
            MqttClient client = mqttService.getClient(router.getFromServer());
            try {
                client.subscribe(router.getFromTopic().split(","));
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        forwardService.publish(router.getFromServer(), message.getPayload());
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            } catch (MqttException e) {
                log.error("初始化MQTT连接失败", e);
            }

        }
    }
}
