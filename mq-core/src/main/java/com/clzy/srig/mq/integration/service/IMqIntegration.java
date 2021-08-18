package com.clzy.srig.mq.integration.service;

import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.enums.MQIntegration;

import java.util.List;

public interface IMqIntegration {

    MQIntegration.ServerType type();

    void onPublich(ForwardRouter router, byte[] message);

    /**
     * 初始化连接
     *
     * @param routers
     */
    void initReceiver(List<ForwardRouter> routers);

    /**
     * 建立连接
     *
     * @param router
     */
    void connect(ForwardRouter router);

    /**
     * 删除连接
     *
     * @param router
     */
    void disConnect(ForwardRouter router);

    /**
     * 测试连接
     *
     * @param router
     * @return
     */
    boolean testConnect(ForwardRouter router);
}
