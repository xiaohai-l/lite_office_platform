package io.github.xiaohai.lite_office_platform.controller;

import io.github.xiaohai.lite_office_platform.common.R;
import io.github.xiaohai.lite_office_platform.entity.Role;
import io.github.xiaohai.lite_office_platform.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public R<Role> create(@RequestBody Role role) {
        return R.success(roleService.createRole(role));
    }

    @PutMapping("/{id}")
    public R<Role> update(@PathVariable Long id, @RequestBody Role role) {
        return R.success(roleService.updateRole(id, role));
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.success("删除成功");
    }

    @GetMapping
    public R<List<Role>> getAll() {
        return R.success(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public R<Role> getById(@PathVariable Long id) {
        return R.success(roleService.getById(id));
    }
}