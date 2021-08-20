package com.clzy.srig.mq.integration.config;

import com.clzy.geo.core.common.persistence.annotation.MyBatisDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@MapperScan(basePackages = "com.clzy", annotationClass = MyBatisDao.class)
public class MqIntegrationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
