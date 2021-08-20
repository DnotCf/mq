package com.clzy.srig.mq.integration.entity;

import com.clzy.geo.core.common.persistence.DataEntity;
import lombok.*;

import java.util.Date;

/**
 * 路由转发
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardRouter extends DataEntity<ForwardRouter> {

    private MQServer fromServer;

    private String fromTopic;

    private MQServer toServer;

    private String toTopic;

    /**
     * 服务状态0异常,1正常运行
     */
    private String status;

    /**
     * 过期时间
     */
    private Date expireTime;

    private Integer sourceType;

    private String type;
    private String networkType;
    private String name;

    @Override
    public void doPreInsert() {

    }

    @Override
    public void doPreUpdate() {

    }

    public ForwardRouter(String id) {
        super(id);
    }
}
