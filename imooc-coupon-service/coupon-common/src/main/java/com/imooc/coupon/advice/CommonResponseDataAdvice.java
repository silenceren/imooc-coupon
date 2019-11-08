package com.imooc.coupon.advice;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @program: imooc-coupon
 * @description:
 * @author: tianwei
 * @create: 2019-10-10 12:55
 */

@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否需要对响应进行处理
     * @param methodParameter
     * @param aClass
     * @return false: 不需要处理; true: 需要处理
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        //如果当前方法所在的类标识了 @IgnoreResponseAdvice 注解，不需要处理
        if(methodParameter.getDeclaringClass().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )){
            return false;
        }

        //如果当前方法标识了@IgnoreResponseAdvice 注解，不需要处理
        if(methodParameter.getMethod().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )){
            return false;
        }

        //对响应进行处理，执行 beforeBodyWrite 方法
        return true;
    }

    /**
     * 响应返回之前的处理
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        //定义最终的返回对象
        CommonResponse<Object> response = new CommonResponse<>(
                0,""
        );

        //如果 o 是 null ，reponse 不需要设置 data
        if ( null == o){
            return response;
            //如果 o 已经是 CommonReponse， 不需要再次处理
        }else if (o instanceof CommonResponse){
            response = (CommonResponse<Object>) o;
            //否则，把响应对象作为 CommonReponse 的 data 部分
        }else{
            response.setData(o);
        }
        return response;
    }
}
