package com.clzy.srig.http.service;

import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.MQServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: tangs
 * @Date: 2021/8/18 11:26
 */
@Service
public class HttpIntegrationService {

    private RestTemplate restTemplate = new RestTemplate();

    public void createConnect(MQServer server) {
        String url = getConectionUrl(server);

    }

    public String getConectionUrl(MQServer server) {
        String namesrvAddr = server.getCluster();
        if (StringUtils.isBlank(namesrvAddr)) {
            namesrvAddr = String.format("%s://%s:%d", server.getProtocol(), server.getIp(), server.getPort());
        }
        return namesrvAddr;
    }

}
