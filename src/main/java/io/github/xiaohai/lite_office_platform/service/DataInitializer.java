package io.github.xiaohai.lite_office_platform.service.init;

import io.github.xiaohai.lite_office_platform.entity.Department;
import io.github.xiaohai.lite_office_platform.entity.Role;
import io.github.xiaohai.lite_office_platform.entity.User;
import io.github.xiaohai.lite_office_platform.repository.DepartmentRepository;
import io.github.xiaohai.lite_office_platform.repository.RoleRepository;
import io.github.xiaohai.lite_office_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 系统数据初始化器
 * 应用启动后自动执行
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    // 可以添加开关控制，例如通过配置文件中某个属性决定是否初始化
    // @Value("${app.init-data:false}")
    // private boolean shouldInitData;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. 初始化角色（先于用户，因为用户需要关联角色）
        initRoles();

        // 2. 初始化部门
        initDepartments();

        // 3. 初始化管理员用户
        initAdminUser();

        log.info("系统基础数据初始化完成！");
    }

    private void initRoles() {
        if (roleRepository.count() > 0) {
            log.info("角色表已有数据，跳过初始化");
            return;
        }

        // 超级管理员角色（拥有所有权限）
        Role adminRole = new Role();
        adminRole.setCode("SUPER_ADMIN");
        adminRole.setName("超级管理员");
        adminRole.setDescription("系统最高权限管理员，拥有所有权限");
        adminRole.setPermissions(Arrays.asList(
                "user:view", "user:create", "user:edit", "user:delete", "user:assign_role",
                "dept:view", "dept:create", "dept:edit", "dept:delete",
                "role:view", "role:create", "role:edit", "role:delete", "role:assign_permission",
                "task:view", "task:create", "task:edit", "task:delete", "task:assign"
        ));

        // 普通用户角色（基础权限）
        Role userRole = new Role();
        userRole.setCode("NORMAL_USER");
        userRole.setName("普通用户");
        userRole.setDescription("系统普通用户，拥有基础权限");
        userRole.setPermissions(Arrays.asList(
                "user:view",      // 只能查看用户信息（自己）
                "dept:view",      // 查看部门
                "task:view",      // 查看任务
                "task:create",    // 创建任务
                "task:edit"       // 编辑自己的任务
        ));

        // 部门管理员角色（示例）
        Role deptAdminRole = new Role();
        deptAdminRole.setCode("DEPT_ADMIN");
        deptAdminRole.setName("部门管理员");
        deptAdminRole.setDescription("部门管理员，管理本部门事务");
        deptAdminRole.setPermissions(Arrays.asList(
                "user:view", "user:create", "user:edit",
                "dept:view", "dept:edit",
                "task:view", "task:create", "task:edit", "task:delete", "task:assign"
        ));

        roleRepository.saveAll(Arrays.asList(adminRole, userRole, deptAdminRole));
        log.info("初始化了3个角色: 超级管理员, 普通用户, 部门管理员");
    }

    private void initDepartments() {
        if (departmentRepository.count() > 0) {
            log.info("部门表已有数据，跳过初始化");
            return;
        }

        // 创建根部门（公司）
        Department rootDept = new Department();
        rootDept.setName("示例科技有限公司");
        rootDept.setDescription("公司总部");
        rootDept.setSortOrder(1);

        // 创建一级子部门
        Department dept1 = new Department();
        dept1.setName("技术研发部");
        dept1.setDescription("负责产品研发和技术支持");
        dept1.setSortOrder(1);

        Department dept2 = new Department();
        dept2.setName("市场运营部");
        dept2.setDescription("负责市场推广和客户运营");
        dept2.setSortOrder(2);

        Department dept3 = new Department();
        dept3.setName("人力资源部");
        dept3.setDescription("负责人才招聘和员工发展");
        dept3.setSortOrder(3);

        // 建立树形关系
        rootDept.addChild(dept1);
        rootDept.addChild(dept2);
        rootDept.addChild(dept3);

        // 保存（级联保存子部门）
        departmentRepository.save(rootDept);
        log.info("初始化了部门树: 1个根部门 + 3个子部门");
    }

    private void initAdminUser() {
        if (userRepository.count() > 0) {
            log.info("用户表已有数据，跳过初始化");
            return;
        }

        // 获取超级管理员角色
        Role adminRole = roleRepository.findByCode("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("超级管理员角色不存在，请先初始化角色"));

        // 获取根部门（可选）
        Department rootDept = departmentRepository.findByParentIsNullOrderBySortOrderAsc()
                .stream().findFirst().orElse(null);

        // 创建管理员用户
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123")); // 🔐 初始密码，首次登录后应修改
        adminUser.setNickname("系统管理员");
        adminUser.setEmail("admin@example.com");
        adminUser.setRoles(Arrays.asList(adminRole)); // 分配角色

        if (rootDept != null) {
            adminUser.setDepartment(rootDept); // 分配到根部门
        }

        userRepository.save(adminUser);
        log.info("初始化了管理员用户: 用户名=admin, 密码=admin123");
        log.warn("⚠️ 请务必在首次登录后修改管理员密码！");
    }
}