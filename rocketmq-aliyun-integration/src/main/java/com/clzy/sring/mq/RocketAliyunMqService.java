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
import com.clzy.srig.mq.integration.enums.MQStuats;
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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: tangs
 * @Date: 2021/8/16 16:52
 */
@Slf4j
@Service
public class RocketAliyunMqService {


    private ConcurrentHashMap<String, ConsumerBean> consumerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ProducerBean> producerMap = new ConcurrentHashMap<>();

    @Autowired
    private ForwardService forwardService;

    public synchronized ConsumerBean buildOrderConsumer(ForwardRouter router) {
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
        log.info("==={}===ConsumerBean ?????????rocketmq????????????=====", conectionUrl);
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
        //????????????
        Properties properties = mqConfig.getMqPropertie();
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqConfig.getOrderGroupId());
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums, param.getString(PropertyKeyConst.ConsumeThreadNums));
        properties.setProperty(PropertyKeyConst.ConsumeTimeout, param.getString(PropertyKeyConst.ConsumeTimeout));
        properties.setProperty(PropertyKeyConst.MessageModel, param.getString(PropertyKeyConst.MessageModel));
        orderConsumerBean.setProperties(properties);
        //????????????
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(mqConfig.getOrderTopic());
        subscription.setExpression(mqConfig.getOrderTag());
        subscriptionTable.put(subscription, new MessageListener() {
            @Override
            public Action consume(Message message, ConsumeContext context) {
                try {
                    log.debug("AliYunMQ Receive: " + message);
                    //do something..
                    forwardService.publish(router.getFromServer(), message.getBody());
                    return Action.CommitMessage;
                } catch (Exception e) {
                    //????????????
                    log.error("====rocketAliyun????????????", e);
                    return Action.ReconsumeLater;
                }
            }
        });
        //????????????topic???????????????
        orderConsumerBean.setSubscriptionTable(subscriptionTable);
//        consumerMap.put(conectionUrl, orderConsumerBean);
        log.info("==={}===ConsumerBean ?????????rocketmq????????????=====", conectionUrl);
        return orderConsumerBean;
    }

    public void testConnection(ForwardRouter router) {
        if (router.getToServer() != null) {
            ProducerBean producerBean = buildProducer(router);
//            String tag = router.getToServer().getTag();
//            if (StringUtils.isBlank(tag)) {
//                tag = "*";
//            }
//            Message msg = new Message(router.getToTopic(), tag, "testConnection".getBytes(StandardCharsets.UTF_8));
//            producerBean.send(msg);
            producerBean.shutdown();
            return;
        }
        if (StringUtils.isBlank(router.getFromTopic())) {
            router.setFromTopic("testConnection");
        }
        String conectionUrl = getConectionUrl(router.getFromServer());
        log.info("==={}===ConsumerBean ?????????rocketmq????????????=====", conectionUrl);
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
        //????????????
        Properties properties = mqConfig.getMqPropertie();
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqConfig.getOrderGroupId());
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums, param.getString(PropertyKeyConst.ConsumeThreadNums));
        properties.setProperty(PropertyKeyConst.ConsumeTimeout, param.getString(PropertyKeyConst.ConsumeTimeout));
        properties.setProperty(PropertyKeyConst.MessageModel, param.getString(PropertyKeyConst.MessageModel));
        orderConsumerBean.setProperties(properties);
        //????????????
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(mqConfig.getOrderTopic());
        subscription.setExpression(mqConfig.getOrderTag());
        final Boolean[] cos = {true};
        subscriptionTable.put(subscription, new MessageListener() {
            @Override
            public Action consume(Message message, ConsumeContext context) {
                log.debug("AliYunMQ Receive: " + message);
                try {
                    //do something..
                    cos[0] = false;
//                    forwardService.publish(router.getFromServer(), message.getBody());
                    return Action.CommitMessage;
                } catch (Exception e) {
                    //????????????
                    log.error("====rocketAliyun????????????", e);
                    return Action.ReconsumeLater;
                }
            }
        });
        //????????????topic???????????????
        orderConsumerBean.setSubscriptionTable(subscriptionTable);
        orderConsumerBean.start();
        int i=0;
        while (cos[0] && i < param.getInteger("waitSec")) {
            try {
                Thread.sleep(1000);
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        orderConsumerBean.shutdown();
        if (cos[0]) {
            throw new RuntimeException("AliyunRocketMQ??????????????????!!!");
        }
    }

    public  ProducerBean buildProducer(ForwardRouter server) {
        String conectionUrl = getConectionUrl(server.getToServer());
        log.info("==={}===ProducerBean ?????????rocketmq????????????=====", conectionUrl);
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
        //??????
        orderProducerBean.start();
        log.info("==={}===ProducerBean ?????????rocketmq????????????=====", conectionUrl);
        return orderProducerBean;
    }

    public synchronized ProducerBean buildOrderProducer(ForwardRouter server) {
        String conectionUrl = getConectionUrl(server.getToServer());
        if (producerMap.get(conectionUrl) != null) {
            return producerMap.get(conectionUrl);
        }
        ProducerBean producerBean = buildProducer(server);
        producerMap.put(conectionUrl, producerBean);
        return producerBean;
    }

    public String getConectionUrl(MQServer server) {
        String namesrvAddr = server.getCluster();
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        }
        return namesrvAddr.trim();
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
        if (object.getInteger("waitSec") == null) {
            object.put("waitSec", 10);
        }
        return object;
    }

    public synchronized void disConnect(MQServer server) {
        String url = getConectionUrl(server);
        ConsumerBean consumerBean = consumerMap.get(url);
        if (consumerBean != null) {
            consumerBean.shutdown();
            consumerMap.remove(url);
        }
    }

    public synchronized void disProducerConnect(MQServer server) {
        String url = getConectionUrl(server);
        ProducerBean producerBean = producerMap.get(url);
        if (producerBean != null) {
            producerBean.shutdown();
            consumerMap.remove(url);
        }
    }

}
