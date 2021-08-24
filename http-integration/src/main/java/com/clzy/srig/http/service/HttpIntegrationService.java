package com.clzy.srig.http.service;

import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardRouterService;
import com.clzy.srig.mq.integration.service.ForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
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
    public static ConcurrentHashMap<String, ScheduledFuture> runTasks = new ConcurrentHashMap<>(16);


    //线程池任务调度
    private ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    /**
     * 初始化线程池任务调度
     */
    @Autowired
    public HttpIntegrationService(){
        this.threadPoolTaskScheduler.setPoolSize(16);
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
        runTasks.put(url, threadPoolTaskScheduler.schedule(() -> consumer(server), Instant.parse(param.getString("cron"))));
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
//        if (server.getNetworkType().equals("0") && StringUtils.isNotBlank(server.getVpnAccount())) {
//            RestTemplate restTemplate1 = new RestTemplate();
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            // 请注意，我这里是在241机器上，借助tinyproxy搭建了一个http的代理，并设置端口为18888，所以可以正常演示代理访问
//            // 拉源码运行的小伙，需要注意使用自己的代理来替换
//            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(server.getIp(), server.getPort())));
//            restTemplate1.setRequestFactory(requestFactory);
//            MultiValueMap map = new LinkedMultiValueMap();
//            ResponseEntity<String> post = restTemplate1.postForEntity(url, map, String.class);
//            if (post.getStatusCode().equals(HttpStatus.OK)) {
//                forwardService.publish(server, post.getBody().getBytes(StandardCharsets.UTF_8));
//            }
//        }else {

            MultiValueMap map = new LinkedMultiValueMap();
            ResponseEntity<String> post = restTemplate.postForEntity(url, map, String.class);
            if (post.getStatusCode().equals(HttpStatus.OK)) {
                forwardService.publish(server, post.getBody().getBytes(StandardCharsets.UTF_8));
            }
//        }
    }

    public void getConsumer(MQServer server, String url, JSONObject param) {
        String object = null;
//        if (server.getNetworkType().equals("0") && StringUtils.isNotBlank(server.getVpnAccount())) {
//            RestTemplate restTemplate1 = new RestTemplate();
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//
//            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(server.getIp(), server.getPort())));
//
//            restTemplate1.setRequestFactory(requestFactory);
//            HttpEntity<String> ans =
//                    restTemplate1.getForEntity(url, String.class);
//            object = ans.getBody();
//        }else {
        object = restTemplate.getForObject(url, String.class);
//        }
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
            object.put("method", "GET");
        }
        return object;
    }

    /**
     * Java发送请求---HttpURLConnection方式
     */
    public  void readContentFromGet(MQServer server) throws Exception{
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(server.getCluster());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(server.getIp(), server.getPort()));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
            //设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            httpURLConnection.disconnect();
            //todo
            forwardService.publish(server, buffer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
