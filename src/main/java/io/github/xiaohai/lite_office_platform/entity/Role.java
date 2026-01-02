package io.github.xiaohai.lite_office_platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色实体
 * 采用轻量设计：权限直接以字符串集合形式存储在本实体中
 */
@Entity
@Table(name = "sys_role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // 角色编码，如：ADMIN、USER、DEPT_MANAGER

    @Column(nullable = false, length = 50)
    private String name; // 角色名称，如：系统管理员、普通用户、部门经理

    private String description;

    // ---------- 轻量化核心：权限字符串集合 ----------
    // 存储格式示例：["user:view", "user:edit", "dept:manage"]
    @ElementCollection(fetch = FetchType.EAGER) // 急加载，因为权限判断频繁
    @CollectionTable(name = "sys_role_permission", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    private List<String> permissions = new ArrayList<>();

    // ---------- 与用户的关联（由User端维护，这里只是反向引用） ----------
    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    // ---------- 审计字段 ----------
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}