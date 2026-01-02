package io.github.xiaohai.lite_office_platform.security;

import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 添加这行
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 获取用户的所有权限（从角色中扁平化提取）
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream()) // 将每个角色的权限列表扁平化
                .map(SimpleGrantedAuthority::new) // 转换为Spring Security的Authority对象
                .collect(Collectors.toList());

        log.info("用户 [{}] 加载了角色和权限", username);

        // 构建UserDetails，注入权限
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities) //  关键：这里改为.authorities()，而不是.roles()
                .build();
    }
}