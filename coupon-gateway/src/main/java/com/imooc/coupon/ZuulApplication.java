package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @program: imooc-coupon
 * @description: 网关应用启动入口
 * @author: tianwei
 * @create: 2019-10-08 14:54
 */

@EnableZuulProxy  //表示当前应用是zuul server
@SpringCloudApplication
public class ZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);
    }
}
