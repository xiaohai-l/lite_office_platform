package io.github.xiaohai.lite_office_platform.controller;

import io.github.xiaohai.lite_office_platform.common.R;
import io.github.xiaohai.lite_office_platform.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-role")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("/assign/{userId}")
    public R<String> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userRoleService.assignRolesToUser(userId, roleIds);
        return R.success("角色分配成功");
    }

    @GetMapping("/user/{userId}")
    public R<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        return R.success(userRoleService.getRoleIdsByUserId(userId));
    }
}