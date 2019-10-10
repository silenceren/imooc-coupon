package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: imooc-coupon
 * @description: 在过滤器中存储客户端发起的请求时间戳
 * @author: tianwei
 * @create: 2019-10-09 09:10
 */

@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter{

    @Override
    protected Object cRun() {

        context.set("startTime", System.currentTimeMillis());

        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
