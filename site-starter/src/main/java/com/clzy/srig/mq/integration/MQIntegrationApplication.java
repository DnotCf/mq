package com.clzy.srig.mq.integration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.clzy", exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class MQIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MQIntegrationApplication.class, args);
    }
}
