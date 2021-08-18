package com.clzy.srig.mq.integration.entity;

import com.clzy.geo.core.common.persistence.DataEntity;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import lombok.*;

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

    private String secretKey;
    private String accessKey;
    private String group;
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