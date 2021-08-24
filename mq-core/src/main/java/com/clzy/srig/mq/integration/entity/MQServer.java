package com.clzy.srig.mq.integration.entity;

import com.clzy.geo.core.common.persistence.DataEntity;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(value = {
        "dbName","delFlag","deleted","errorCnt","errorList","errorReportByErrorList","isNewRecord","page"
        ,"preFlag","sqlMap","updateDate","isNewRecord","remarks","createBy","createUserName","updateBy",
        "updateUserName","deleted","orgUnitId","orgUnitName","extAttr1","extAttr2","extAttr3","extAttr4","extAttr5","extAttr6",
        "extAttr7","extAttr8","extAttr9","extAttr10","extAttr11","extAttr12","extAttr13","extAttr14","extAttr15"
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MQServer extends DataEntity<MQServer> {

    /**
     * 消息服务类型
     */
    private MQIntegration.ServerType type;

    /**
     * 网络协议
     */
    private String protocol;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 服务器地址
     */
    private String ip;

    /**
     * 服务端口
     */
    private Integer port;

    /**
     * 客户端连接名称
     */
    private String clientName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 服务状态
     */
    private String status;

    /**
     * 重试次数
     */
    private Integer retry;

    /**
     * 集群地址 ip:prot,ip2:prot2
     */
    private String cluster;

    /**
     * 默认连接参数
     */
    private String defaultParam;
    /**
     * 标签
     */
    private String tag;
    /**
     * 阿里云key
     */
    private String secretKey;
    /**
     * 阿里云key
     */
    private String accessKey;
    /**
     * mq消费gruopId
     */
    private String group;
    /**
     * 网络类型 0内网，1外网
     */
    private String networkType;
    /**
     * vpn账号
     */
    private String vpnAccount;
    /**
     * vpn密码
     */
    private String vpnPassword;
    /**
     * 代理服务地址
     */
    private String vpnAddress;

    private String topic;

    private Integer sourceType;

    @Override
    public void doPreInsert() {

    }

    @Override
    public void doPreUpdate() {

    }

    public MQServer(String id) {
        super(id);
    }

}
