package com.clzy.sring.mq;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.bean.*;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardService;
import com.clzy.sring.mq.config.MqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:52
 */
@Slf4j
@Service
public class RocketAliyunMqService {


    private Map<String, ConsumerBean> consumerMap = new HashMap<>();
    private Map<String, ProducerBean> producerMap = new HashMap<>();

    @Autowired
    private ForwardService forwardService;

    public ConsumerBean buildOrderConsumer(ForwardRouter router) {
        String conectionUrl = getConectionUrl(router.getFromServer());
        if (consumerMap.get(conectionUrl) != null) {
            return consumerMap.get(conectionUrl);
        }
        ConsumerBean orderConsumerBean = build(router);
        orderConsumerBean.start();
        consumerMap.put(conectionUrl, orderConsumerBean);
        return orderConsumerBean;
    }

    public ConsumerBean build(ForwardRouter router) {
        String conectionUrl = getConectionUrl(router.getFromServer());
        log.info("==={}===阿里云rocketmq连接开始=====", conectionUrl);
        MqConfig mqConfig = new MqConfig();
        JSONObject param = getDefaultParam(router.getFromServer().getDefaultParam());
        mqConfig.setSecretKey(router.getFromServer().getSecretKey());
        mqConfig.setAccessKey(router.getFromServer().getAccessKey());
        mqConfig.setOrderGroupId(router.getFromServer().getGroup());
        mqConfig.setNameSrvAddr(conectionUrl);
        mqConfig.setOrderTopic(router.getFromTopic());
        String tag = router.getFromServer().getTag();
        if (StringUtils.isBlank(tag)) {
            tag = "*";
        }
        mqConfig.setOrderTag(tag);

        ConsumerBean orderConsumerBean = new ConsumerBean();
        //配置文件
        Properties properties = mqConfig.getMqPropertie();
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqConfig.getOrderGroupId());
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums, param.getString(PropertyKeyConst.ConsumeThreadNums));
        properties.setProperty(PropertyKeyConst.ConsumeTimeout, param.getString(PropertyKeyConst.ConsumeTimeout));
        properties.setProperty(PropertyKeyConst.MessageModel, param.getString(PropertyKeyConst.MessageModel));
        orderConsumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(mqConfig.getOrderTopic());
        subscription.setExpression(mqConfig.getOrderTag());
        subscriptionTable.put(subscription, new MessageListener() {
            @Override
            public Action consume(Message message, ConsumeContext context) {
                log.debug("AliYunMQ Receive: " + message);
                try {
                    //do something..
                    forwardService.publish(router.getFromServer(), message.getBody());
                    return Action.CommitMessage;
                } catch (Exception e) {
                    //消费失败
                    log.error("====rocketAliyun消费错误", e);
                    return Action.ReconsumeLater;
                }
            }
        });
        //订阅多个topic如上面设置
        orderConsumerBean.setSubscriptionTable(subscriptionTable);
//        consumerMap.put(conectionUrl, orderConsumerBean);
        log.info("==={}===阿里云rocketmq连接成功=====", conectionUrl);
        return orderConsumerBean;
    }

    public void testConnection(ForwardRouter router) {
        ConsumerBean build = build(router);
        build.start();
        build.shutdown();
    }

    public ProducerBean buildOrderProducer(ForwardRouter server) {
        String conectionUrl = getConectionUrl(server.getFromServer());
        if (producerMap.get(conectionUrl) != null) {
            return producerMap.get(conectionUrl);
        }
        MqConfig mqConfig = new MqConfig();
//        JSONObject param = getDefaultParam(server.getToServer().getDefaultParam());
        mqConfig.setSecretKey(server.getToServer().getSecretKey());
        mqConfig.setAccessKey(server.getToServer().getAccessKey());
        mqConfig.setNameSrvAddr(conectionUrl);
//        mqConfig.setOrderGroupId(param.getString("groupId"));
//        mqConfig.setOrderTopic(server.getToTopic());
//        mqConfig.setOrderTag(param.getString("tag"));
        ProducerBean orderProducerBean = new ProducerBean();
        orderProducerBean.setProperties(mqConfig.getMqPropertie());
        //启
        orderProducerBean.start();
        producerMap.put(conectionUrl, orderProducerBean);
        return orderProducerBean;
    }

    public String getConectionUrl(MQServer server) {
        String namesrvAddr = server.getCluster();
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        }
        return namesrvAddr;
    }

    public JSONObject getDefaultParam(String jsonStr) {
        JSONObject object = null;
        if (StringUtils.isBlank(jsonStr)) {
            object = new JSONObject();
        } else {
            object = JSONObject.parseObject(jsonStr);
        }
        if (StringUtils.isBlank(object.getString("tag"))) {
            object.put("tag", "*");
        }
        if (StringUtils.isBlank(object.getString("group"))) {
            object.put("group", MQIntegration.defaultGroupName);
        }
        if (StringUtils.isBlank(object.getString(PropertyKeyConst.ConsumeThreadNums))) {
            object.put(PropertyKeyConst.ConsumeThreadNums, "1");
        }
        if (StringUtils.isBlank(object.getString(PropertyKeyConst.ConsumeTimeout))) {
            object.put(PropertyKeyConst.ConsumeTimeout, "1000");
        }
        if (StringUtils.isBlank(object.getString(PropertyKeyConst.MessageModel))) {
            object.put(PropertyKeyConst.MessageModel, "BROADCASTING");
        }
        return object;
    }

    public void disConnect(MQServer server) {
        String url = getConectionUrl(server);
        ConsumerBean consumerBean = consumerMap.get(url);
        if (consumerBean != null) {
            consumerBean.shutdown();
            consumerMap.remove(url);
        }
    }

}
