package com.clzy.srig.mq.integration;

import com.alibaba.druid.sql.visitor.functions.Concat;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Predicates;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = "com.clzy")
public class MQIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MQIntegrationApplication.class, args);
    }

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(new ApiInfo("api", "mq转发", "1.0", "21", new Contact("1", "123", "123@qq.com"), "213", "213"))
                .select()
                //这一句可加可不加,该配置可以扫描所有有ApiOperation注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

}
