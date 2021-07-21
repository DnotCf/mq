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
    private int port;


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


    @Override
    public void doPreInsert() {

    }

    @Override
    public void doPreUpdate() {

    }
}
