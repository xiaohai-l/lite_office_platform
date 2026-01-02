package io.github.xiaohai.lite_office_platform.service.impl;

import io.github.xiaohai.lite_office_platform.entity.Role;
import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.repository.RoleRepository;
import io.github.xiaohai.lite_office_platform.repository.UserRepository;
import io.github.xiaohai.lite_office_platform.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        // 获取所有要分配的角色
        List<Role> rolesToAssign = roleRepository.findAllById(roleIds);
        if (rolesToAssign.size() != roleIds.size()) {
            throw new RuntimeException("部分角色不存在");
        }

        // 清空现有角色并设置新角色
        user.getRoles().clear();
        user.getRoles().addAll(rolesToAssign);

        userRepository.save(user);
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        return user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        return user.getRoles();
    }
}