package dao;

import entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @program: imooc-coupon
 * @description:
 * @author: tianwei
 * @create: 2020-05-12 17:11
 */
public interface PathRepository extends JpaRepository<Path, Integer> {

    // 根据服务名称查找 path 记录
    List<Path> findAllByServiceName(String serviceName);

    // 根据 路径模式 + 请求类型 查找数据记录
    Path findByPathPatternAndHttpMethod(String pathPattern, String httpMethod);

}
