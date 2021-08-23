package com.clzy.srig.http.service;

import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardRouterService;
import com.clzy.srig.mq.integration.service.ForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author: tangs
 * @Date: 2021/8/18 11:26
 */
@Service
public class HttpIntegrationService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ForwardService forwardService;

    //正在运行的任务
    public static ConcurrentHashMap<String, ScheduledFuture> runTasks = new ConcurrentHashMap<>(10);

    //线程池任务调度
    private ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    /**
     * 初始化线程池任务调度
     */
    @Autowired
    public HttpIntegrationService(){
        this.threadPoolTaskScheduler.setPoolSize(10);
        this.threadPoolTaskScheduler.setThreadNamePrefix("http-consumer-thread-");
        this.threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        this.threadPoolTaskScheduler.initialize();
    }

    public void createConnect(MQServer server) {
        String url = getConectionUrl(server);
        if (runTasks.get(url) != null) {
            return;
        }
        JSONObject param = getDefaultParam(server.getDefaultParam());
        runTasks.put(url, threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                consumer(server);
            }
        }, Instant.parse(param.getString("cron"))));
    }

    public void disConnect(MQServer server) {
        String url = getConectionUrl(server);
        if (runTasks.get(url) != null) {
            ScheduledFuture future = runTasks.get(url);
            future.cancel(true);
            runTasks.remove(url);
            return;
        }
    }

    public boolean testConnect(MQServer server) {
        try {
            String url = getConectionUrl(server);
            MultiValueMap map = new LinkedMultiValueMap();
            JSONObject param = getDefaultParam(server.getDefaultParam());
            if ("POST".equals(param.getString("method"))) {
                ResponseEntity<String> post = restTemplate.postForEntity(url, map, String.class);
                if (post.getStatusCode().equals(HttpStatus.OK)) {
                    forwardService.publish(server, post.getBody().getBytes(StandardCharsets.UTF_8));
                    return true;
                }
            } else {
                String object = restTemplate.getForObject(url, String.class);
                if (StringUtils.isNotBlank(object)) {
                    return true;
                }
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void postConsumer(MQServer server, String url, JSONObject param) {
        MultiValueMap map = new LinkedMultiValueMap();
        ResponseEntity<String> post = restTemplate.postForEntity(url, map, String.class);
        if (post.getStatusCode().equals(HttpStatus.OK)) {
            forwardService.publish(server, post.getBody().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void getConsumer(MQServer server, String url, JSONObject param) {
        String object = restTemplate.getForObject(url, String.class);
        if (StringUtils.isNotBlank(object)) {
            forwardService.publish(server, object.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void consumer(MQServer server) {
        String url = getConectionUrl(server);
        JSONObject param = getDefaultParam(server.getDefaultParam());
        String method = param.getString("method");
        if ("POST".equals(method)) {
            postConsumer(server, url, param);
        }else {
            getConsumer(server, url, param);
        }
    }

    public String getConectionUrl(MQServer server) {
        String namesrvAddr = server.getCluster();
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        }
        return namesrvAddr.trim();
    }

    public JSONObject getDefaultParam(String jsonStr) {
        JSONObject object = null;
        if (StringUtils.isBlank(jsonStr)) {
            object = new JSONObject();
        } else {
            object = JSONObject.parseObject(jsonStr);
        }
        if (object.getString("cron") == null) {
            object.put("cron", "0 0/1 * * * ?");

        }
        if (StringUtils.isBlank(object.getString("method"))) {
            object.put("method", "POST");
        }
        return object;
    }
}
