package com.clzy.srig.http.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: tangs
 * @Date: 2021/8/18 10:53
 */
@Service
public class HttpIntegration implements IMqIntegration {
    @Autowired
    private HttpIntegrationService service;
    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.HTTP;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {
        //todo http推送
    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {
        for (ForwardRouter router : routers) {
            service.createConnect(router.getFromServer());
        }
    }

    @Override
    public void connect(ForwardRouter router) {
        service.createConnect(router.getFromServer());
    }

    @Override
    public void disConnect(ForwardRouter router) {
        service.disConnect(router.getFromServer());
    }

    @Override
    public boolean testConnect(ForwardRouter router) {
        return service.testConnect(router.getFromServer());
    }
}
