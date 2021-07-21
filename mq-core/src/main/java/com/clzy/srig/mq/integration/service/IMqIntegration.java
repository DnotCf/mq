package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.enums.MQIntegration;

import java.util.List;

public interface IMqIntegration {

    MQIntegration.ServerType type();

    void onPublich(ForwardRouter router, byte[] message);

    void initReceiver(List<ForwardRouter> routers);
}
