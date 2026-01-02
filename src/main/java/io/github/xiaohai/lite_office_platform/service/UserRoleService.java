package io.github.xiaohai.lite_office_platform.service;

import java.util.List;

public interface UserRoleService {
    /** 为用户分配角色 */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /** 获取用户的角色ID列表 */
    List<Long> getRoleIdsByUserId(Long userId);

    /** 获取用户的所有角色 */
    List<io.github.xiaohai.lite_office_platform.entity.Role> getRolesByUserId(Long userId);
}