package io.github.xiaohai.lite_office_platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体
 * 支持树形结构
 */
@Entity
@Table(name = "sys_dept") // 表名
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name; // 部门名称

    @Column(length = 500)
    private String description; // 部门描述

    private Integer sortOrder = 0; // 排序号（同级部门显示顺序）

    // ---------- 关键：树形结构关系 ----------
    @ManyToOne(fetch = FetchType.LAZY)//这个注解表示多对一，只记录parent_id就行，不需要记录Chlidren_id字段
    @JoinColumn(name = "parent_id") // 数据库中的父部门ID字段
    @ToString.Exclude // 避免 Lombok toString 循环引用
    private Department parent; // 父部门

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)//这个注解表示一对多
    @OrderBy("sortOrder ASC") // 子部门按排序号正序排列
    @ToString.Exclude
    private List<Department> children = new ArrayList<>(); // 子部门列表

    // ---------- 与用户的关系 ----------
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<User> users = new ArrayList<>(); // 部门下的用户

    // ---------- 审计字段 ----------
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    /**
     * 添加子部门的便捷方法（维护双向关联）
     */
    public void addChild(Department child) {
        children.add(child);
        child.setParent(this);
    }
}