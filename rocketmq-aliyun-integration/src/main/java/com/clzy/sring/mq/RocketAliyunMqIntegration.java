package com.clzy.sring.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.enums.MQStuats;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:47
 */
@Slf4j
@Service
public class RocketAliyunMqIntegration implements IMqIntegration {

    @Autowired
    private RocketAliyunMqService aliyunMqService;

    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.Aliyun_RocketMQ;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {
        MQServer server = router.getToServer();
        try {
            ProducerBean producer = aliyunMqService.buildOrderProducer(router);
            String tag = server.getTag();
            if (StringUtils.isBlank(tag)) {
                tag = "*";
            }
            Message msg = new Message(router.getToTopic(), tag, message);
            producer.send(msg);
            router.setStatus(MQStuats.online.getCode());
        } catch (Exception e) {
            log.error("AliYunRocketMq消息推送失败");
            aliyunMqService.disProducerConnect(server);
            router.setStatus(MQStuats.client_offline.getCode());
            e.printStackTrace();
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
            aliyunMqService.buildOrderConsumer(router);
        } catch (Exception e) {
            router.setStatus(MQStuats.server_offline.getCode());
            log.error("初始化RocketMq连接失败", e);
        }
    }

    @Override
    public void disConnect(ForwardRouter router) {
        aliyunMqService.disConnect(router.getFromServer());
        router.setStatus(MQStuats.offline.getCode());
    }

    @Override
    public boolean testConnect(ForwardRouter router) {
        try {
            aliyunMqService.testConnection(router);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
