package dao;

import entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Long> {

    // 通过 userId 寻找数据记录
    UserRoleMapping findByUserId(Long userId);

}
