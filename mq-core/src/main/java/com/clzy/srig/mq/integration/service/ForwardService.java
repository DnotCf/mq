package com.clzy.srig.mq.integration.service;

import com.alibaba.fastjson.JSON;
import com.clzy.geo.core.utils.Collections3;
import com.clzy.geo.core.utils.DateUtils;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.enums.MQStuats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@EnableScheduling
@Component
public class ForwardService {

    @Autowired
    private List<IMqIntegration> messagePublishes;

    @Autowired
    private ForwardRouterService forwardRouterService;

    private ConcurrentHashMap<String, List<ForwardRouter>> routerTable = new ConcurrentHashMap<>();

    private ExecutorService service = null;

    public void publish(MQServer mqServer, byte[] message) {

        List<ForwardRouter> list = getRouter(mqServer);

        if (Collections3.isEmpty(list)) {
            return;
        }

//        Collection<ForwardRouter> routers = Collections.synchronizedCollection(list);

        for (ForwardRouter router : list) {
            if (router.getExpireTime() == null || router.getExpireTime().compareTo(new Date()) > 0) {
                messagePublishes.forEach(p -> {
                    if (p.type().equals(router.getToServer().getType())) {
                        service.execute(() -> {
                            p.onPublich(router, message);
                            router.setUpdateDate(new Date());
                        });
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
        if (service == null) {
            service = Executors.newFixedThreadPool(messagePublishes.size());
        }

//        Map<MQIntegration.ServerType, List<ForwardRouter>> routerMap = new HashMap<>();

        for (ForwardRouter router : list) {
//            List<ForwardRouter> temps = routerMap.computeIfAbsent(router.getFromServer().getType(), k -> new ArrayList<>());
//            temps.add(router);
            if (router.getExpireTime() == null || router.getExpireTime().compareTo(new Date()) >= 0) {
                addRouterTable(router);
            }
        }

//        routerMap.forEach((serverType, routers) -> {
//            messagePublishes.stream().filter(p -> p.type().equals(serverType)).forEach(c -> c.initReceiver(routers));
//        });
    }

    /**
     * ???????????????
     *
     * @param router
     */
    public void addRouterTable(ForwardRouter router) {
        if (router == null) {
            return;
        }
        log.info("====={}=??????{}==???topic:{},??????topic:{}=???????????????=====", DateUtils.getDateTime(), router.getFromServer().getName(), router.getFromTopic(), router.getToTopic());
        check(router);
        String fromId = router.getFromServer().getId();
        List<ForwardRouter> routers = routerTable.get(fromId);
        if (CollectionUtils.isEmpty(routers)) {
            routers = new ArrayList<>();
            routers.add(router);
            routerTable.put(fromId, routers);
            //???????????????
            List<ForwardRouter> finalRouters = routers;
            messagePublishes.forEach(s -> {
                if (s.type().equals(router.getFromServer().getType())) {
//                    log.info("=====?????????==={}==", JSON.toJSONString(finalRouters.get(0)));
                    s.initReceiver(finalRouters);
                }
            });
        } else {
            if (!routers.contains(router)) {
                routers.add(router);
            }
        }
    }

    /**
     * ???????????????
     *
     * @param router
     */
    public void deleteRouterTable(ForwardRouter router) {
        if (router == null) {
            return;
        }
        log.info("====={}=??????{}=??????id:{}=topic:{}=???????????????=====", DateUtils.getDateTime(), router.getFromServer().getName(),router.getId(), router.getFromTopic());
        check(router);
        String fromId = router.getFromServer().getId();
        List<ForwardRouter> routers = routerTable.get(fromId);
        if (!CollectionUtils.isEmpty(routers)) {
            routers.removeIf(router1 -> router1.getToServer().getId().equals(router.getToServer().getId()));
        }
        if (CollectionUtils.isEmpty(routers)) {
            stopConsumer(router);
            routerTable.remove(fromId);
        }
    }

    public void clearRouterTable() {
        Collection<List<ForwardRouter>> values = routerTable.values();
        if (CollectionUtils.isEmpty(values)) {
            log.info("=====?????????????????????????????????=====");
            return;
        }
        for (List<ForwardRouter> list : values) {
            for (ForwardRouter router : list) {
                stopConsumer(router);
            }
        }
        routerTable.clear();
    }

    public void clearRouterTable(String fromServerId) {
        List<ForwardRouter> list = routerTable.get(fromServerId);
        if (CollectionUtils.isEmpty(list)) {
            routerTable.remove(fromServerId);
            return;
        }
        for (ForwardRouter router : list) {
            stopConsumer(router);
        }
        routerTable.remove(fromServerId);
    }

    public void stopConsumer(ForwardRouter router) {
        log.info("====={}=??????{}=??????id:{}=topic:{}=????????????=====", DateUtils.getDateTime(), router.getFromServer().getName(),router.getId(), router.getFromTopic());
        messagePublishes.forEach(p -> {
            if (p.type().equals(router.getFromServer().getType())) {
                try {
                    p.disConnect(router);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (StringUtils.isNotBlank(router.getId())) {
            router.setExpireTime(null);
            router.setStatus(MQStuats.offline.getCode());
            forwardRouterService.updateStatus(router);
        }
    }

    public void startConsumer(ForwardRouter router) {
        if (router == null) {
            return;
        }
        log.info("====={}=??????{}=??????id:{}=topic:{}=????????????=====", DateUtils.getDateTime(), router.getFromServer().getName(),router.getId(), router.getFromTopic());
        messagePublishes.forEach(p -> {
            if (p.type().equals(router.getFromServer().getType())) {
                try {
                    p.connect(router);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ???????????????
     *
     * @param router
     */
    public void updateRouterTable(ForwardRouter router) {
        if (router == null) {
            return;
        }
        log.info("====={}=??????{}=??????id:{}=topic:{}=???????????????=====", DateUtils.getDateTime(), router.getFromServer().getName(),router.getId(), router.getFromTopic());
        check(router);
        String fromId = router.getFromServer().getId();
        List<ForwardRouter> routers = routerTable.get(fromId);
        if (!CollectionUtils.isEmpty(routers)) {
            routers.removeIf(router1 -> router1.getToServer().getId().equals(router.getToServer().getId()));
        } else {
            routers = new ArrayList<>();
            routerTable.put(fromId, routers);
        }
        routers.add(router);
        stopConsumer(router);
        startConsumer(router);
    }

    private void check(ForwardRouter router) {
        String fromId = router.getFromServer().getId();
        if (router.getFromServer() == null || StringUtils.isBlank(fromId)) {
            throw new RuntimeException("?????????????????????id??????");
        }
        if (router.getToServer() == null || StringUtils.isBlank(router.getToServer().getId())) {
            throw new RuntimeException("????????????????????????id??????");
        }
    }

    public boolean testConnection(ForwardRouter router) {
        for (IMqIntegration publish : messagePublishes) {
            if ((router.getFromServer() !=null && publish.type().equals(router.getFromServer().getType())) ||
                    (router.getToServer() !=null && publish.type().equals(router.getToServer().getType()))
            ) {
                return publish.testConnect(router);
            }
        }
        return false;
    }

    @Scheduled(fixedRate = 1000 * 60 * 1)
    public void statusSync() {
//        log.info("==={}==??????????????????????????????=====", DateUtils.getDateTime());
        Collection<List<ForwardRouter>> values = routerTable.values();
        if (CollectionUtils.isEmpty(values)) {
            log.info("=====??????????????????????????????????????????=====");
            return;
        }
        Integer online = 0;
        Integer clientOffline = 0;
        Integer serverOffline = 0;
        Integer toatl = 0;
        for (List<ForwardRouter> list : values) {
            for (ForwardRouter router : list) {
                if (router.getUpdateDate() == null || (System.currentTimeMillis() - router.getUpdateDate().getTime()) > 1000d * 3600 * 24) {
                    router.setStatus(MQStuats.offline.getCode());
                }
                if (router.getExpireTime() != null && router.getExpireTime().compareTo(new Date()) < 0) {
                    deleteRouterTable(router);
                    router.setStatus(MQStuats.expire.getCode());
                } else {
                    if (MQStuats.online.getCode().equals(router.getStatus())) {
                        online++;
                    } else if (MQStuats.client_offline.getCode().equals(router.getStatus())) {
                        clientOffline++;
                    } else {
                        serverOffline++;
                    }
                }
                if (router.getToServer() != null) {
                    router.getToServer().setRetry(1);
                }
                toatl++;
                forwardRouterService.updateStatus(router);
            }
        }
        log.info("==={}==??????????????????????????????{},?????????{}(??????????????????{},??????????????????{})????????????{}=====", DateUtils.getDateTime(),
                online, clientOffline + serverOffline, serverOffline, clientOffline, toatl);
    }
}
