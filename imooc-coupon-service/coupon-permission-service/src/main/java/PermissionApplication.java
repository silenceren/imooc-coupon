import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @program: imooc-coupon
 * @description: 权限服务启动程序
 * @author: tianwei
 * @create: 2020-05-12 16:43
 */
@EnableEurekaClient
@SpringBootApplication
public class PermissionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionApplication.class, args);
    }

}
