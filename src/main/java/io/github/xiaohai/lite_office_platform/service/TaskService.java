package io.github.xiaohai.lite_office_platform.service;

import io.github.xiaohai.lite_office_platform.dto.TaskDTO;
import io.github.xiaohai.lite_office_platform.dto.TaskDetailDTO;
import io.github.xiaohai.lite_office_platform.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Map;

public interface TaskService {
    Task createTask(TaskDTO dto, Long reporterId);
    Task updateTask(Long id, TaskDTO dto);
    void deleteTask(Long id);
    TaskDetailDTO getTaskDetail(Long id);

    // 查询方法
    Page<TaskDetailDTO> searchTasks(String title, Task.TaskStatus status,
                                    Task.TaskPriority priority, Long assigneeId,
                                    Long departmentId, LocalDate dueDateFrom,
                                    LocalDate dueDateTo, Pageable pageable);

    Page<TaskDetailDTO> getMyTasks(Long userId, Pageable pageable);
    Page<TaskDetailDTO> getAssignedTasks(Long userId, Pageable pageable);
    Page<TaskDetailDTO> getDepartmentTasks(Long departmentId, Pageable pageable);

    // 统计
    Map<String, Long> getTaskStats(Long userId);
    Map<String, Long> getDepartmentTaskStats(Long departmentId);

    // 状态流转
    Task changeStatus(Long taskId, Task.TaskStatus newStatus, Long operatorId);
    Task reassignTask(Long taskId, Long newAssigneeId, Long operatorId);
}