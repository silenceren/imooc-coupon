package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 权限校验请求对象定义
 * @author: tianwei
 * @create: 2020-05-12 15:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPermissionRequest {

    private Long userId;
    private String uri;
    private String httpMethod;

}
