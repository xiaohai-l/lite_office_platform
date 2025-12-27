package io.github.xiaohai.lite_office_platform.security;

import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务
 * 这是Spring Security查询用户的核心接口
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 使用我们之前写的Repository查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        log.info("用户 [{}] 正在尝试登录", username);

        // 2. 将我们的 User 实体，转换为 Spring Security 能识别的 UserDetails 对象
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // 这里是从数据库查出的加密密码
                .roles("USER") // 暂时赋予一个默认角色“USER”，后续可改为从数据库读取
                .build();
    }
}