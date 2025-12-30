package io.github.xiaohai.lite_office_platform.service.impl;

import io.github.xiaohai.lite_office_platform.dto.DepartmentDTO;
import io.github.xiaohai.lite_office_platform.dto.DepartmentTreeDTO;
import io.github.xiaohai.lite_office_platform.entity.Department;
import io.github.xiaohai.lite_office_platform.repository.DepartmentRepository;
import io.github.xiaohai.lite_office_platform.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department createDepartment(DepartmentDTO dto) {
        Department department = new Department();
        BeanUtils.copyProperties(dto, department, "id", "parentId");

        // 处理父部门
        if (dto.getParentId() != null) {
            Department parent = getById(dto.getParentId());
            department.setParent(parent);
        }

        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, DepartmentDTO dto) {
        Department department = getById(id);
        BeanUtils.copyProperties(dto, department, "id", "parentId");

        // 更新父部门（如果需要）
        if (dto.getParentId() != null &&
                (department.getParent() == null || !department.getParent().getId().equals(dto.getParentId()))) {
            Department newParent = getById(dto.getParentId());
            // 防止循环引用：不能将自己或自己的后代设为父部门
            if (isDescendantOrSelf(newParent, id)) {
                throw new RuntimeException("不能将部门设置到自己的子部门下");
            }
            department.setParent(newParent);
        } else if (dto.getParentId() == null) {
            department.setParent(null); // 设为顶级部门
        }

        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = getById(id);

        // 业务规则1：有子部门不能删除
        if (!department.getChildren().isEmpty()) {
            throw new RuntimeException("该部门下存在子部门，无法删除");
        }

        // 业务规则2：有员工不能删除（可选，根据需求）
        if (!department.getUsers().isEmpty()) {
            throw new RuntimeException("该部门下存在员工，无法删除");
        }
        // 如果使用Repository的查询方法：
        // if (departmentRepository.existsUsersInDepartment(id)) {
        //     throw new RuntimeException("该部门下存在员工，无法删除");
        // }

        departmentRepository.deleteById(id);
        log.info("删除部门：{}", id);
    }

    @Override
    public Department getById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在：" + id));
    }

    @Override
    public List<DepartmentTreeDTO> getDepartmentTree() {
        // 1. 获取所有顶级部门
        List<Department> roots = departmentRepository.findByParentIsNullOrderBySortOrderAsc();
        // 2. 递归构建树，递归操作在convertToTreeDTO中进行，一位内史私有方法，所以命名空间是this
        return roots.stream().map(this::convertToTreeDTO).collect(Collectors.toList());
    }

    @Override
    public List<Department> getChildren(Long parentId) {
        if (parentId == null) {
            return departmentRepository.findByParentIsNullOrderBySortOrderAsc();
        }
        return departmentRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }

    // ---------- 私有辅助方法 ----------

    /**
     * 将Department实体转换为树形DTO（递归）
     */
    private DepartmentTreeDTO convertToTreeDTO(Department dept) {
        DepartmentTreeDTO dto = new DepartmentTreeDTO();
        BeanUtils.copyProperties(dept, dto, "parent", "children", "users");

        // 设置父部门ID
        if (dept.getParent() != null) {
            dto.setParentId(dept.getParent().getId());
        }

        // 设置用户数量
        dto.setUserCount(dept.getUsers() != null ? dept.getUsers().size() : 0);

        // 递归处理子部门
        List<DepartmentTreeDTO> childDTOs = dept.getChildren().stream()
                .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());
        dto.setChildren(childDTOs);

        return dto;
    }

    /**
     * 检查目标部门是否是当前部门的子孙部门或自己
     * 用于防止循环引用
     */
    private boolean isDescendantOrSelf(Department target, Long currentId) {
        if (target == null) return false;
        if (target.getId().equals(currentId)) return true; // 是自己

        // 递归检查父部门
        Department parent = target.getParent();
        while (parent != null) {
            if (parent.getId().equals(currentId)) {
                return true; // 是祖先
            }
            parent = parent.getParent();
        }
        return false;
    }
}