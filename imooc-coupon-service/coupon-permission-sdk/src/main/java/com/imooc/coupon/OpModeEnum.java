package com.imooc.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: imooc-coupon
 * @description: 操作模式的枚举定义
 * @author: tianwei
 * @create: 2020-05-12 13:50
 */
@Getter
@AllArgsConstructor
public enum OpModeEnum {

    READ("读"),
    WRITE("写");

    private String mode;

}
