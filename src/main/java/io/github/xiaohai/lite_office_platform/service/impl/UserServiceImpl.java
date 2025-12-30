package io.github.xiaohai.lite_office_platform.service.impl;

import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.repository.UserRepository;
import io.github.xiaohai.lite_office_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务逻辑层实现类
 */
@Service // 标识这是一个Spring的业务层Bean
@Slf4j // Lombok注解，自动提供日志对象log，用于记录日志
@RequiredArgsConstructor // Lombok注解，为final字段自动生成构造函数，用于依赖注入
@Transactional // 表示该类所有方法都默认在事务中执行
public class UserServiceImpl implements UserService {

    // 依赖注入UserRepository
    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {//User代表返回的是User类型的数据
        // 使用Optional优雅处理，找到返回用户，没找到返回null
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User createUser(User user) {
        // 这里可以添加创建用户前的业务逻辑，例如再次验证、设置默认角色等
        log.info("创建新用户：{}", user.getUsername()); // 使用log记录信息
        return userRepository.save(user); // JPA的save方法，插入或更新
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        // 存在则不可用，返回false
        return !userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // 在 UserServiceImpl.java 中添加方法实现
    @Override
    public User findById(Long id) {
        return userRepository.findById(id) // JPA内置方法，返回Optional
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User updateUserProfile(User updatedUser) {
        // 1. 从数据库查出当前用户
        User existingUser = findById(updatedUser.getId());

        // 2. 只更新允许修改的字段（避免更新用户名、密码等敏感字段）
        if (updatedUser.getNickname() != null) {
            existingUser.setNickname(updatedUser.getNickname());
        }
        if (updatedUser.getEmail() != null) {
            // 这里可以添加邮箱格式验证和唯一性检查
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getAvatar() != null) {
            existingUser.setAvatar(updatedUser.getAvatar());
        }

        // 3. 保存更新（updateTime会自动更新，因为我们在实体类中加了@UpdateTimestamp）
        return userRepository.save(existingUser);
    }


}