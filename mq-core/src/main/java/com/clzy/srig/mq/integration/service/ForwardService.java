package com.clzy.srig.mq.integration.service;

import com.clzy.geo.core.utils.Collections3;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ForwardService {

    @Autowired
    private List<IMqIntegration> messagePublishes;

    @Autowired
    private ForwardRouterService forwardRouterService;


    public void publish(MQServer mqServer, byte[] message) {

        List<ForwardRouter> list = getRouter(mqServer);

        if (Collections3.isEmpty(list)) {
            return;
        }

        for (ForwardRouter router : list) {
            messagePublishes.forEach(p -> {
                if (p.type().equals(router.getToServer().getType())) {
                    p.onPublich(router, message);
                }
            });
        }

    }

    private List<ForwardRouter> getRouter(MQServer mqServer) {
        return forwardRouterService.findList(ForwardRouter.builder().toServer(mqServer).build());
    }

    private List<ForwardRouter> getRouter() {
        return forwardRouterService.findList(new ForwardRouter());
    }

    @PostConstruct
    public void init() {
        List<ForwardRouter> list = getRouter();

        if (Collections3.isEmpty(list)) {
            return;
        }

        Map<MQIntegration.ServerType, List<ForwardRouter>> routerMap = new HashMap<>();

        for (ForwardRouter router : list) {
            List<ForwardRouter> temps = routerMap.computeIfAbsent(router.getFromServer().getType(), k -> new ArrayList<>());
            temps.add(router);
        }

        messagePublishes.forEach(p -> p.initReceiver(routerMap.get(p.type())));
    }
}
