package com.clzy.srig.http.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: tangs
 * @Date: 2021/8/18 10:53
 */
@Slf4j
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
        log.info("=====http 收到数据：{}====", new String(message));
    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {
        for (ForwardRouter router : routers) {
            service.createConnect(router);
        }
    }

    @Override
    public void connect(ForwardRouter router) {
        service.createConnect(router);
    }

    @Override
    public void disConnect(ForwardRouter router) {
        service.disConnect(router.getFromServer());
    }

    @Override
    public boolean testConnect(ForwardRouter router) {
        try {
            if (router.getFromServer() == null) {
                service.testConnect(router.getToServer());
            }else {
                service.testConnect(router.getFromServer());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
