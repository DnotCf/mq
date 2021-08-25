package com.clzy.sring.mq;

import com.alibaba.fastjson.JSON;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.enums.MQStuats;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:47
 */
@Slf4j
@Service
public class KafkaMqIntegration implements IMqIntegration {

    @Autowired
    private KafkaMqService kafkaMqService;

    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.Kafka;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {
        MQServer server = router.getToServer();
        try {
            if (server.getRetry() != null && server.getRetry() < 0) {
                return;
            }
            log.debug("===={} send msg：{}===", type(), new String(message));
            KafkaProducer producer = kafkaMqService.createProducer(server);
            String[] topic = server.getTopic().split(",");
            for (String tp : topic) {
                ProducerRecord<String, String> record =
                        new ProducerRecord<>(tp, new String(message));
                producer.send(record);
            }
            router.setStatus(MQStuats.online.getCode());
        } catch (Exception e) {
            log.error("RocketMq消息推送失败");
            router.setStatus(MQStuats.client_offline.getCode());
            e.printStackTrace();
            if (server.getRetry() == null || server.getRetry() > 0) {
                server.setRetry(-1);
                kafkaMqService.disConnectProducer(server);
                try {
                    log.info("=====RocketMq producer重试连接=====");
                    KafkaProducer producer = kafkaMqService.createProducer(server);
                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(server.getTopic(), new String(message));
                    producer.send(record);
                    router.setStatus(MQStuats.online.getCode());
                } catch (Exception e1) {
                    router.setStatus(MQStuats.client_offline.getCode());
                    log.error("=====RocketMq producer重试连接失败=====");
                }

            }
        }
    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {
        for (ForwardRouter router : routers) {
            connect(router);
        }
    }

    @Override
    public void connect(ForwardRouter router) {
        try {
            KafkaConsumer consumer = kafkaMqService.createConsumer(router);
        } catch (Exception e) {
            router.setStatus(MQStuats.server_offline.getCode());
            log.error("初始化RocketMq连接失败", e);
        }
    }

    @Override
    public void disConnect(ForwardRouter router) {
        try {
            kafkaMqService.disConnect(router.getFromServer());
            router.setStatus(MQStuats.offline.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean testConnect(ForwardRouter router) {
        try {
            kafkaMqService.testConnect(router);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
