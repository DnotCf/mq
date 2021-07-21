package com.clzy.srig.mq.integration.dao;

import com.clzy.geo.core.common.persistence.CrudDao;
import com.clzy.geo.core.common.persistence.annotation.MyBatisDao;
import com.clzy.srig.mq.integration.entity.ForwardRouter;

@MyBatisDao
public interface ForwardRouterDao extends CrudDao<ForwardRouter> {
}
