package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @program: imooc-coupon
 * @description: 模板微服务的启动入口
 * @author: tianwei
 * @create: 2019-11-04 15:46
 */
@EnableScheduling
@EnableJpaAuditing
@EnableEurekaClient
@SpringBootApplication
public class TemplateApplication {

    public static void main(String[] args){
        SpringApplication.run(TemplateApplication.class, args);
    }


}
