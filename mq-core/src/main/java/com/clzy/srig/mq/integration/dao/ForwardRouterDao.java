package com.clzy.srig.mq.integration.dao;

import com.clzy.geo.core.common.persistence.CrudDao;
import com.clzy.geo.core.common.persistence.annotation.MyBatisDao;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface ForwardRouterDao extends CrudDao<ForwardRouter> {
    int updateStatus(ForwardRouter entity);
}
