package com.clzy.srig.http.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.IMqIntegration;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: tangs
 * @Date: 2021/8/18 10:53
 */
@Service
public class HttpIntegration implements IMqIntegration {
    @Override
    public MQIntegration.ServerType type() {
        return MQIntegration.ServerType.HTTP;
    }

    @Override
    public void onPublich(ForwardRouter router, byte[] message) {

    }

    @Override
    public void initReceiver(List<ForwardRouter> routers) {

    }

    @Override
    public void disConnect(ForwardRouter router) {

    }
}
