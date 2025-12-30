package io.github.xiaohai.lite_office_platform.repository;
import io.github.xiaohai.lite_office_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
//JPA自带CRUD功能，即读写改查
//本接口用来声明查找用户，和用户名、邮箱判空功能
//JPA的命名规则采用find。。。by，或者exists。。。by
@Repository // 标识这是一个Spring的数据仓库Bean，可省略，但显式声明更清晰，告诉编译器从bean里面选择
public interface UserRepository extends JpaRepository<User, Long> { // <实体类型, 主键类型>

    /**
     * 根据用户名查找用户，
     * JPA会根据方法名自动生成SQL：SELECT * FROM sys_user WHERE username = ?
     * @param username 用户名
     * @return 包含用户的Optional对象，防止空指针
     */
    Optional<User> findByUsername(String username);

    /**
     * 判断用户名是否存在
     * JPA会自动生成SQL：SELECT COUNT(*) FROM sys_user WHERE username = ?
     * @param username 用户名
     * @return 存在返回true，否则false
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否存在
     * @param email 邮箱
     * @return 存在返回true，否则false
     */
    boolean existsByEmail(String email);
}
