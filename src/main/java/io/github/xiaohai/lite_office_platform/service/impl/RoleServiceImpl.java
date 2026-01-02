package io.github.xiaohai.lite_office_platform.service.impl;

import io.github.xiaohai.lite_office_platform.entity.Role;
import io.github.xiaohai.lite_office_platform.repository.RoleRepository;
import io.github.xiaohai.lite_office_platform.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        if (roleRepository.existsByCode(role.getCode())) {
            throw new RuntimeException("角色编码已存在: " + role.getCode());
        }
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long id, Role role) {
        Role existing = getById(id);
        // 检查编码唯一性（排除自己）
        if (!existing.getCode().equals(role.getCode()) &&
                roleRepository.existsByCode(role.getCode())) {
            throw new RuntimeException("角色编码已存在: " + role.getCode());
        }
        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        existing.setPermissions(role.getPermissions()); // 更新权限列表
        return roleRepository.save(existing);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = getById(id);
        // 检查是否有用户关联
        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("该角色下存在关联用户，无法删除");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public Role getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
    }

    @Override
    public Role getByCode(String code) {
        return roleRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + code));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}