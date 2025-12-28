package io.github.xiaohai.lite_office_platform.controller;

import io.github.xiaohai.lite_office_platform.common.R;
import io.github.xiaohai.lite_office_platform.common.SecurityUtil;
import io.github.xiaohai.lite_office_platform.dto.UserProfileDTO;
import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 处理用户个人信息相关的请求
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final SecurityUtil securityUtil;//引入构造函数
    /**
     * 获取当前登录用户的信息
     * GET /api/user/me
     */
    @GetMapping("/me")
    public R<UserProfileDTO> getCurrentUser() {
        User user = securityUtil.getCurrentUser();//getCurrentUser()会返回当前登录的用户到user中
        if (user == null) {
            return R.error(2002, "用户不存在或未登录");
        }

        // 将User实体转换为UserProfileDTO，避免暴露密码等敏感字段，因为UserProfileDTO没有密码字段
        UserProfileDTO profileDTO = new UserProfileDTO();
        BeanUtils.copyProperties(user, profileDTO);

        return R.success(profileDTO);
    }

    /**
     * 更新当前登录用户的个人资料
     * PUT /api/user/profile
     */
    @PutMapping("/profile")
    public R<UserProfileDTO> updateProfile(@Valid @RequestBody UserProfileDTO profileDTO) {
        // 1. 使用 SecurityUtil 获取当前登录用户
        User currentUser = securityUtil.getCurrentUser();

        if (currentUser == null) {
            return R.error(2002, "用户不存在或未登录");
        }

        // 2. 确保只能修改自己的资料
        if (!currentUser.getId().equals(profileDTO.getId())) {
            return R.error(2003, "无权修改其他用户信息");
        }

        // 3. 后续的更新和保存逻辑完全保持不变...
        // ... (设置昵称、邮箱、头像)

        // 4. 保存更新
        User updatedUser = userService.updateUserProfile(currentUser);

        // 5. 返回更新后的用户信息
        UserProfileDTO resultDTO = new UserProfileDTO();
        BeanUtils.copyProperties(updatedUser, resultDTO);

        return R.success(resultDTO);
    }
}