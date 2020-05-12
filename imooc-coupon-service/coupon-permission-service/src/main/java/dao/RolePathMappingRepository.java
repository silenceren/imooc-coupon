package dao;

import entity.RolePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePathMappingRepository extends JpaRepository<RolePathMapping, Integer> {

    // 通过  Roles id + 路径id 寻找数据记录
    RolePathMapping findByRoleIdAndPathId(Integer roleId, Integer pathId);

}
