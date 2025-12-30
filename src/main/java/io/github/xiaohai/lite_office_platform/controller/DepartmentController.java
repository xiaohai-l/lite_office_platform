package io.github.xiaohai.lite_office_platform.controller;

import io.github.xiaohai.lite_office_platform.common.R;
import io.github.xiaohai.lite_office_platform.dto.DepartmentDTO;
import io.github.xiaohai.lite_office_platform.dto.DepartmentTreeDTO;
import io.github.xiaohai.lite_office_platform.entity.Department;
import io.github.xiaohai.lite_office_platform.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dept")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    //操作根据Post，Put，DElete区分
    /** 创建部门 */
    @PostMapping
    public R<Department> create(@Valid @RequestBody DepartmentDTO dto) {
        Department dept = departmentService.createDepartment(dto);
        return R.success(dept);
    }

    /** 更新部门 */
    @PutMapping("/{id}")
    public R<Department> update(@PathVariable Long id, @Valid @RequestBody DepartmentDTO dto) {
        Department dept = departmentService.updateDepartment(id, dto);
        return R.success(dept);
    }

    /** 删除部门 */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return R.success("删除成功");
    }

    /** 获取部门详情 */
    @GetMapping("/{id}")
    public R<Department> getById(@PathVariable Long id) {
        Department dept = departmentService.getById(id);
        return R.success(dept);
    }

    /** 获取完整的部门树 */
    @GetMapping("/tree")
    public R<List<DepartmentTreeDTO>> getTree() {
        List<DepartmentTreeDTO> tree = departmentService.getDepartmentTree();
        return R.success(tree);
    }

    /** 获取子部门列表 */
    @GetMapping("/children")
    public R<List<Department>> getChildren(@RequestParam(required = false) Long parentId) {
        List<Department> children = departmentService.getChildren(parentId);
        return R.success(children);
    }
}