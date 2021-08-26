package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.enums.MQStuats;
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
        try {
            if (server.getRetry() != null && server.getRetry() < 0) {
                return;
            }
            log.debug("===={} send msg：{}===", type(), new String(message));
            MqttClient client = mqttService.getClient(server);
            String[] topics = router.getToTopic().split(",");
            for (String topic : topics) {
                client.publish(topic, message, 1, false);
            }
            router.setStatus(MQStuats.online.getCode());
        } catch (MqttException e) {
            log.error("MQTT消息推送失败");
            e.printStackTrace();
            router.setStatus(MQStuats.client_offline.getCode());
            if (server.getRetry() == null || server.getRetry() > 0) {
                server.setRetry(-1);
                mqttService.disConnect(server);
                try {
                    log.info("=====MQTT producer重试连接=====");
                    MqttClient client = mqttService.getClient(server);
                    client.publish(router.getToTopic(), message, 1, false);
                    router.setStatus(MQStuats.online.getCode());
                } catch (Exception e1) {
                    router.setStatus(MQStuats.client_offline.getCode());
                    log.error("=====MQTT producer重试连接失败=====");
                }
            }
        }
    }

    @Override
    public void initReceiver(List<ForwardRouter> forwardRouters) {
        for (ForwardRouter router : forwardRouters) {
            consumer(router);
        }
    }

    public void consumer(ForwardRouter router) {
        try {
            MqttClient client = mqttService.getClient(router.getFromServer());
            client.subscribe(router.getFromTopic().split(","));
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //todo 重新连接
                    log.error("=====断开连接=====", cause);
                    router.setStatus(MQStuats.server_offline.getCode());
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
            router.setStatus(MQStuats.server_offline.getCode());
        }

    }

    @Override
    public void connect(ForwardRouter router) {
        MqttClient client = mqttService.getMqttClientMap(router.getFromServer());
        if (client == null) {
            consumer(router);
        }
    }

    @Override
    public void disConnect(ForwardRouter router) {
        mqttService.disConnect(router.getFromServer());
        router.setStatus(MQStuats.offline.getCode());
    }

    @Override
    public boolean testConnect(ForwardRouter router) {
        try {
            mqttService.testConncet(router);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
