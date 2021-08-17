package com.clzy.srig.mq.integration.entity;

import com.clzy.geo.core.common.persistence.DataEntity;
import lombok.*;

import java.util.Date;

/**
 * 路由转发
 *
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
     * 服务状态0禁用,1正常运行,2运行异常
     */
    private String status;

    /**
     * 过期时间
     */
    private Date expireTime;

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
