package com.clzy.srig.mq.integration.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MQServer {

    /**
     * 消息服务类型
     */
    private String type;

    /**
     * 网络协议
     */
    private String protocol;

    /**
     * 服务器地址
     */
    private String host;

    /**
     * 服务端口
     */
    private int port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


}
