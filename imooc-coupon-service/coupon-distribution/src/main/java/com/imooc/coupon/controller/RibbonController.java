package com.imooc.coupon.controller;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @program: imooc-coupon
 * @description: Ribbon 应用
 * @author: tianwei
 * @create: 2019-12-06 09:52
 */
@Slf4j
@RestController
public class RibbonController {

    private final RestTemplate restTemplate;

    @Autowired
    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 通过 Ribbon 组件调用模板微服务
     * @return
     */
    @GetMapping("/info")
    @IgnoreResponseAdvice
    public TemplateInfo getTemplateInfo() {

        String infoUrl = "http://eureka-client-coupon-template/coupon-template/info";
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }

    /** 模板微服务的元信息 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TemplateInfo {

        private Integer code;
        private String message;
        private List<Map<String, Object>> data;
    }
}
