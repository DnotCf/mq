package com.clzy.srig.mq.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.clzy")
public class MQIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MQIntegrationApplication.class, args);
    }
}
