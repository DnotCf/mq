package com.clzy.srig.mq.integration.entity;

import com.clzy.geo.core.common.persistence.DataEntity;
import lombok.*;

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

    @Override
    public void doPreInsert() {

    }

    @Override
    public void doPreUpdate() {

    }
}
