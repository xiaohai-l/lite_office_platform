package io.github.xiaohai.lite_office_platform.repository;

import io.github.xiaohai.lite_office_platform.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 查找所有顶级部门（parent为null）
     */
    List<Department> findByParentIsNullOrderBySortOrderAsc();

    /**
     * 根据父部门ID查找子部门
     */
    List<Department> findByParentIdOrderBySortOrderAsc(Long parentId);

    /**
     * 检查部门下是否有用户
     * @param deptId 部门ID
     * @return 存在用户返回true
     * 目的为了检测是否能删除
     * 这种查询太复杂了，JPA做不了，所以用SQL语言
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.department.id = :deptId")
    boolean existsUsersInDepartment(Long deptId);
}