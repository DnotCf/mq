package com.clzy.srig.mq.integration.enums;

/**
 * mq状态
 *
 * @Author: tangs
 * @Date: 2021/8/16 14:26
 */
public enum MQStuats {
    offline("0","离线或停用"),
    online("1", "在线"),
//    server_offline("2","数据提供服务离线"),
//    client_offline("3","目标服务离线")
    ;

    private String code;
    private String decr;

    MQStuats(String code, String decr) {
        this.code = code;
        this.decr = decr;
    }

    public String getCode() {
        return code;
    }

    public String getDecr() {
        return decr;
    }
}
