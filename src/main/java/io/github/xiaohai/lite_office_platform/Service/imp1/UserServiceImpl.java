package io.github.xiaohai.lite_office_platform.Service.imp1;

import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.repository.UserRepository;
import io.github.xiaohai.lite_office_platform.Service.UserService;
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
    public User findByUsername(String username) {
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

    // 后续可以轻松添加其他业务方法，如分页查询、更新用户信息等
}