package com.clzy.srig.mq.integration.service;

import com.clzy.geo.core.common.service.CrudService;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.dao.ForwardRouterDao;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ForwardRouterService extends CrudService<ForwardRouterDao, ForwardRouter> {
    @Autowired
    private MQServerService mqServerService;

    @Transactional(readOnly = false)
    @Override
    public void save(ForwardRouter entity) {
        if (entity.getToServer() != null) {
            entity.getToServer().setSourceType(1);
            mqServerService.save(entity.getToServer());
        }
        super.save(entity);
    }

    @Transactional(readOnly = false)
    public Integer updateStatus(ForwardRouter entity) {
        return dao.updateStatus(entity);
    }
}
