package com.imooc.coupon.advice;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: imooc-coupon
 * @description: 全局异常处理
 * RestControllerAdvice: 组合注解, ControllerAdvice + ResponseBody, 是对 RestController 的功能增强
 * @author: tianwei
 * @create: 2019-10-11 15:48
 */

@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 对 CouponException 进行统一处理
     * ExceptionHandler: 可以对指定的异常进行拦截
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(
            HttpServletRequest req, CouponException ex
    ){
        // 统一异常接口的响应
        // 优化: 定义不同类型的异常枚举(异常码和异常信息)
        CommonResponse<String> response = new CommonResponse<>(-1,"business error");
        response.setData(ex.getMessage());
        return response;
    }
}
