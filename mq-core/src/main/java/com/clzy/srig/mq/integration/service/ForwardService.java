package com.clzy.srig.mq.integration.service;

import com.clzy.geo.core.utils.Collections3;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ForwardService {

    @Autowired
    private List<IMqIntegration> messagePublishes;

    @Autowired
    private ForwardRouterService forwardRouterService;

    private ConcurrentHashMap<String, List<ForwardRouter>> routerTable = new ConcurrentHashMap<>();

    public void publish(MQServer mqServer, byte[] message) {

        List<ForwardRouter> list = getRouter(mqServer);

        if (Collections3.isEmpty(list)) {
            return;
        }

        Collections.synchronizedCollection(list);

        for (ForwardRouter router : list) {
            if (router.getExpireTime() == null || router.getExpireTime().compareTo(new Date()) > 0) {
                messagePublishes.forEach(p -> {
                    if (p.type().equals(router.getToServer().getType())) {
                        p.onPublich(router, message);
                    }
                });
            }
        }

    }

    private List<ForwardRouter> getRouter(MQServer mqServer) {
        return routerTable.get(mqServer.getId());
//        return forwardRouterService.findList(ForwardRouter.builder().toServer(mqServer).build());
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
            if (router.getExpireTime() == null || router.getExpireTime().compareTo(new Date()) > 0) {
                addRouterTable(router);
            }
        }

        routerMap.forEach((serverType, routers) -> {
            messagePublishes.stream().filter(p -> p.type().equals(serverType)).forEach(c -> c.initReceiver(routers));
        });
    }

    /**
     * 添加路由表
     * @param router
     */
    public void addRouterTable(ForwardRouter router) {
        check(router);
        String fromId = router.getFromServer().getId();
        List<ForwardRouter> routers = routerTable.get(fromId);
        if (CollectionUtils.isEmpty(routers)) {
            routers = new ArrayList<>();
            routers.add(router);
            routerTable.put(fromId, routers);
        }else {
            routers.add(router);
        }
    }

    /**
     * 删除路由表
     * @param router
     */
    public void deleteRouterTable(ForwardRouter router) {
        check(router);
        String fromId = router.getFromServer().getId();
        List<ForwardRouter> routers = routerTable.get(fromId);
        if (!CollectionUtils.isEmpty(routers)) {
            Iterator<ForwardRouter> iterator = routers.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getToServer().getId().equals(router.getToServer().getId())) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 更新路由表
     * @param router
     */
    public void updateRouterTable(ForwardRouter router) {
        check(router);
        String fromId = router.getFromServer().getId();
        List<ForwardRouter> routers = routerTable.get(fromId);
        if (!CollectionUtils.isEmpty(routers)) {
            Iterator<ForwardRouter> iterator = routers.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getToServer().getId().equals(router.getToServer().getId())) {
                    iterator.remove();
                }
            }
        }else {
            routers = new ArrayList<>();
            routerTable.put(fromId, routers);
        }
        routers.add(router);
    }

    private void check(ForwardRouter router) {
        String fromId = router.getFromServer().getId();
        if (router.getFromServer() == null || StringUtils.isBlank(fromId)) {
            throw new RuntimeException("源路由数据编号id为空");
        }
        if (router.getToServer()==null || StringUtils.isBlank(router.getToServer().getId())) {
            throw new RuntimeException("目标路由数据编号id为空");
        }
    }
}
