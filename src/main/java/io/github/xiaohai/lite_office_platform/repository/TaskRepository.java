package io.github.xiaohai.lite_office_platform.repository;

import io.github.xiaohai.lite_office_platform.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 基础查询
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);
    Page<Task> findByReporterId(Long reporterId, Pageable pageable);
    Page<Task> findByDepartmentId(Long departmentId, Pageable pageable);

    // 状态查询
    Page<Task> findByAssigneeIdAndStatus(Long assigneeId, Task.TaskStatus status, Pageable pageable);
    Page<Task> findByStatus(Task.TaskStatus status, Pageable pageable);

    // 综合条件查询（使用@Query）
    @Query("SELECT t FROM Task t WHERE " +
            "(:title IS NULL OR t.title LIKE %:title%) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
            "(:departmentId IS NULL OR t.department.id = :departmentId) AND " +
            "(:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) AND " +
            "(:dueDateTo IS NULL OR t.dueDate <= :dueDateTo)")
    Page<Task> searchTasks(
            @Param("title") String title,
            @Param("status") Task.TaskStatus status,
            @Param("priority") Task.TaskPriority priority,
            @Param("assigneeId") Long assigneeId,
            @Param("departmentId") Long departmentId,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable);

    // 统计查询
    long countByAssigneeIdAndStatus(Long assigneeId, Task.TaskStatus status);
    long countByDepartmentIdAndStatus(Long departmentId, Task.TaskStatus status);

    // 替换原来的方法为原生SQL查询
    @Query(value = "SELECT * FROM sys_task WHERE due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 3 DAY)",
            nativeQuery = true) // 🔑 关键：添加 nativeQuery = true
    List<Task> findUpcomingTasks();
}