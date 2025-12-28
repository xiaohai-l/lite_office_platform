package io.github.xiaohai.lite_office_platform.controller;

import io.github.xiaohai.lite_office_platform.common.R;
import io.github.xiaohai.lite_office_platform.dto.LoginRequest;
import io.github.xiaohai.lite_office_platform.dto.RegisterRequest;
import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理注册、登录等认证相关请求
 */
@RestController
@RequestMapping("/api/auth") // 所有接口路径以 /api/auth 开头，这句话的意思是告诉前端必须以这种格式包装http请求
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    // 注入所需的服务和组件
    private final UserService userService;//类的具体实现
    private final PasswordEncoder passwordEncoder; // Spring Security的密码编码器，spring security自带的类

    /**
     * 用户注册接口
     * POST /api/auth/register
     */
    @PostMapping("/register")//这句话的意思是如果http请求来到了/register文件夹，用用接下来的方法处理
    public R<String> register(@Valid @RequestBody RegisterRequest request) {
        log.info("注册请求：用户名={}", request.getUsername());

        // 1. 检查用户名和邮箱是否可用
        if (!userService.isUsernameAvailable(request.getUsername())) {
            return R.error(1001, "用户名已存在");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank() && !userService.isEmailAvailable(request.getEmail())) {
            return R.error(1002, "邮箱已被注册");
        }

        // 2. 创建用户实体，并加密密码,这里的setXXX函数由@data注解自动生成
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); //  关键：加密密码
        newUser.setNickname(request.getNickname());
        newUser.setEmail(request.getEmail());
        // avatar等其他字段可留空或设默认值

        // 3. 保存用户
        userService.createUser(newUser);

        log.info("用户注册成功：{}", newUser.getUsername());
        return R.success("注册成功");//这里体现了R文件的重要性，不需要重写success数据
    }

    /**
     * 用户登录接口
     * POST /api/auth/login
     * 注意：这个接口的实际认证工作是由Spring Security的过滤器链完成的。
     * 我们配置的 .loginProcessingUrl("/api/auth/login") 会拦截此请求进行认证。
     * 认证成功或失败后，会跳转到successHandler 或 failureHandler。
     * 因此，这个控制器方法理论上不会被直接调用，它是一个"声明"或备用路径。
     * 但为了API文档的完整性，我们通常还是会保留它。
     */
    @PostMapping("/login")
    public R<String> login(@Valid @RequestBody LoginRequest request) {
        // 这个方法的逻辑通常不会被执行，因为请求会被Spring Security拦截。
        // 保留它主要用于API文档生成，或者未来可能改变认证方式。
        return R.error("请通过Spring Security认证端点登录");
    }
}