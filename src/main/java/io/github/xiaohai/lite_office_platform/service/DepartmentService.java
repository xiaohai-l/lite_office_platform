package io.github.xiaohai.lite_office_platform.service;

import io.github.xiaohai.lite_office_platform.dto.DepartmentDTO;
import io.github.xiaohai.lite_office_platform.dto.DepartmentTreeDTO;
import io.github.xiaohai.lite_office_platform.entity.Department;
import java.util.List;

public interface DepartmentService {
    /** 创建部门 */
    Department createDepartment(DepartmentDTO dto);

    /** 更新部门 */
    Department updateDepartment(Long id, DepartmentDTO dto);

    /** 删除部门 */
    void deleteDepartment(Long id);

    /** 获取单个部门详情 */
    Department getById(Long id);

    /** 获取完整的部门树 */
    List<DepartmentTreeDTO> getDepartmentTree();

    /** 获取子部门列表（平铺） */
    List<Department> getChildren(Long parentId);
}