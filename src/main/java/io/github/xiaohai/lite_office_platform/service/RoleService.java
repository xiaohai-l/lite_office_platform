package io.github.xiaohai.lite_office_platform.service;

import io.github.xiaohai.lite_office_platform.entity.Role;
import java.util.List;

public interface RoleService {
    Role createRole(Role role);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
    Role getById(Long id);
    Role getByCode(String code);
    List<Role> getAllRoles();
}