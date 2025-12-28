package io.github.xiaohai.lite_office_platform.common;

import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全上下文工具类
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserService userService;

    /**
     * 获取当前登录的用户实体
     * @return 当前登录的User对象
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//从Security上下文获取当前user
        if (authentication == null || !authentication.isAuthenticated()) {//如果等于null，说明未登录
            return null;
        }
        String username = authentication.getName();//否则返回用户名
        return userService.findByUsername(username);//由用户名查询完整用户信息并返回
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID，未登录返回null
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}