package com.clzy.srig.mq.integration.dao;

import com.clzy.geo.core.common.persistence.CrudDao;
import com.clzy.geo.core.common.persistence.annotation.MyBatisDao;
import com.clzy.srig.mq.integration.entity.MQServer;

@MyBatisDao
public interface MQServerDao extends CrudDao<MQServer> {
}
