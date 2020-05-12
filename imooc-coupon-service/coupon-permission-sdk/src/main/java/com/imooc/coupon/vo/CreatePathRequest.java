package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: imooc-coupon
 * @description: 路径创建请求对象定义
 * @author: tianwei
 * @create: 2020-05-12 15:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePathRequest {

    private List<PathInfo> pathInfos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PathInfo {

        // 路径模式
        private String pathPattern;

        // 方法类型
        private String httpMethord;

        // 路径名称
        private String pathName;

        /** 服务名称 */
        private String serviceName;

        // 操作模式: READ, WRITE
        private String opMode;

    }

}
