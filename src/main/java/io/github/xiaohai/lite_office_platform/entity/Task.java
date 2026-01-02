package io.github.xiaohai.lite_office_platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务实体
 */
@Entity
@Table(name = "sys_task")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title; // 任务标题

    @Column(columnDefinition = "TEXT")
    private String description; // 任务描述

    // ---------- 关键属性 ----------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO; // 任务状态

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM; // 优先级

    private LocalDate dueDate; // 截止日期

    private Integer estimatedHours; // 预估工时（小时）

    // ---------- 关联关系 ----------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee; // 负责人

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // 创建人/报告人

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department; // 所属部门（可选）

    // ---------- 审计字段 ----------
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    // ---------- 内嵌类：任务状态枚举 ----------
    public enum TaskStatus {
        TODO("待开始"),
        IN_PROGRESS("进行中"),
        REVIEW("审核中"),
        DONE("已完成"),
        CANCELLED("已取消");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ---------- 内嵌类：优先级枚举 ----------
    public enum TaskPriority {
        LOW("低"),
        MEDIUM("中"),
        HIGH("高"),
        URGENT("紧急");

        private final String displayName;

        TaskPriority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}