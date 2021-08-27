package com.clzy.srig.mq.integration.entity;

import com.clzy.geo.core.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

/**
 * 路由转发
 */
@JsonIgnoreProperties(value = {
        "dbName","delFlag","deleted","errorCnt","errorList","errorReportByErrorList","isNewRecord","page"
        ,"preFlag","sqlMap","isNewRecord","remarks","createBy","createUserName","updateBy",
        "updateUserName","deleted","orgUnitId","orgUnitName","extAttr1","extAttr2","extAttr3","extAttr4","extAttr5","extAttr6",
        "extAttr7","extAttr8","extAttr9","extAttr10","extAttr11","extAttr12","extAttr13","extAttr14","extAttr15"
})
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

    private String fromId;

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
