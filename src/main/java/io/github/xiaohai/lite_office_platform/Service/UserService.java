package io.github.xiaohai.lite_office_platform.Service;
import io.github.xiaohai.lite_office_platform.entity.User;
/**
 * 用户业务逻辑层接口
 */
public interface UserService {

    /**
     * 根据用户名查找用户（用于登录）
     *
     * @param username 用户名
     * @return 用户实体，未找到时返回null
     */
    User findByUsername(String username);

    /**
     * 创建（注册）一个新用户
     *
     * @param user 用户实体（密码需在调用前加密）
     * @return 创建成功的用户实体
     */
    User createUser(User user);

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 可用返回true，不可用返回false
     */
    boolean isUsernameAvailable(String username);

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 可用返回true，不可用返回false
     */
    boolean isEmailAvailable(String email);
}