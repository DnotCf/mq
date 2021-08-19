package com.clzy.srig.mq.integration.controller;

import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;

/**
 * @Author: tangs
 * @Date: 2021/8/19 16:13
 */

public class BaseController {
    public String check(MQServer entity) {
        String msg = null;
        if (entity.getType() == null) {
            return "数据源类型不为空";
        }
        if (StringUtils.isBlank(entity.getName())) {
            return "数据源名称不为空";
        }
        if (MQIntegration.ServerType.Aliyun_RocketMQ.equals(entity.getType())) {
            if (StringUtils.isBlank(entity.getCluster())) {
                return "服务地址不为空";
            }
            if (StringUtils.isBlank(entity.getSecretKey()) || StringUtils.isBlank(entity.getAccessKey())) {
                return "阿里云授权：secretKey、accessKey不为空";
            }
            if (StringUtils.isBlank(entity.getGroup())) {
                return "groupId组不为空";
            }
            if (StringUtils.isBlank(entity.getTag())) {
                return "tag不为空";
            }
            if (StringUtils.isBlank(entity.getTopic())) {
                return "topic不为空";
            }
        }else if (MQIntegration.ServerType.RocketMQ.equals(entity.getType())){
            if (StringUtils.isBlank(entity.getCluster()) && (StringUtils.isBlank(entity.getIp()) || null == entity.getPort())) {
                return "服务地址或ip+端口不为空";
            }
            if (StringUtils.isBlank(entity.getTopic())) {
                return "topic不为空";
            }

        }else if (MQIntegration.ServerType.MQTT.equals(entity.getType())){
            if (StringUtils.isBlank(entity.getProtocol())) {
                return "传输协议不为空";
            }
            if (StringUtils.isBlank(entity.getIp())) {
                return "ip地址不为空";
            }
            if (null == entity.getPort()) {
                return "端口不为空";
            }
            if (StringUtils.isBlank(entity.getClientName())) {
                return "客户端名称不为空";
            }
            if (StringUtils.isBlank(entity.getTopic())) {
                return "topic不为空";
            }
        }else if (MQIntegration.ServerType.AMQP.equals(entity.getType())){
//            if (StringUtils.isBlank(entity.getProtocol())) {
//                return "传输协议不为空";
//            }
            if (StringUtils.isBlank(entity.getIp())) {
                return "ip地址不为空";
            }
            if (null == entity.getPort()) {
                return "端口不为空";
            }
            if (StringUtils.isBlank(entity.getUsername()) || StringUtils.isBlank(entity.getPassword())) {
                return "用户名密码不为空";
            }
            if (StringUtils.isBlank(entity.getClientName())) {
                return "客户端名称不为空";
            }
            if (StringUtils.isBlank(entity.getTopic())) {
                return "topic不为空";
            }
        }else if (MQIntegration.ServerType.HTTP.equals(entity.getType())){

        }else {
            return "没有匹配的数据源类型：MQTT, AMQP, Aliyun_RocketMQ, RocketMQ, HTTP";
        }
        return msg;
    }
}
