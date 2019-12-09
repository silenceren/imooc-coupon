package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @program: imooc-coupon
 * @description: 优惠券结算微服务的启动入口
 * @author: tianwei
 * @create: 2019-12-09 14:32
 */
@EnableEurekaClient
@SpringBootApplication
public class SettlementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SettlementApplication.class, args);
    }

}
