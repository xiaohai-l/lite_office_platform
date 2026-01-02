package io.github.xiaohai.lite_office_platform.service.impl;

import io.github.xiaohai.lite_office_platform.dto.TaskDTO;
import io.github.xiaohai.lite_office_platform.dto.TaskDetailDTO;
import io.github.xiaohai.lite_office_platform.entity.*;
import io.github.xiaohai.lite_office_platform.repository.DepartmentRepository;
import io.github.xiaohai.lite_office_platform.repository.TaskRepository;
import io.github.xiaohai.lite_office_platform.repository.UserRepository;
import io.github.xiaohai.lite_office_platform.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Task createTask(TaskDTO dto, Long reporterId) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task, "id", "assigneeId", "departmentId");

        // 设置创建人
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("创建人不存在"));
        task.setReporter(reporter);

        // 设置负责人
        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("负责人不存在"));
            task.setAssignee(assignee);
        }

        // 设置部门
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("部门不存在"));
            task.setDepartment(department);
        }

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 更新基础字段
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getEstimatedHours() != null) task.setEstimatedHours(dto.getEstimatedHours());

        // 更新负责人
        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("负责人不存在"));
            task.setAssignee(assignee);
        }

        // 更新部门
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("部门不存在"));
            task.setDepartment(department);
        }

        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("任务不存在");
        }
        taskRepository.deleteById(id);
        log.info("删除任务: {}", id);
    }

    @Override
    public TaskDetailDTO getTaskDetail(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        return convertToDetailDTO(task);
    }

    @Override
    public Page<TaskDetailDTO> searchTasks(String title, Task.TaskStatus status,
                                           Task.TaskPriority priority, Long assigneeId,
                                           Long departmentId, LocalDate dueDateFrom,
                                           LocalDate dueDateTo, Pageable pageable) {
        Page<Task> tasks = taskRepository.searchTasks(
                title, status, priority, assigneeId, departmentId,
                dueDateFrom, dueDateTo, pageable);
        return tasks.map(this::convertToDetailDTO);
    }

    @Override
    public Page<TaskDetailDTO> getMyTasks(Long userId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByReporterId(userId, pageable);
        return tasks.map(this::convertToDetailDTO);
    }

    @Override
    public Page<TaskDetailDTO> getAssignedTasks(Long userId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAssigneeId(userId, pageable);
        return tasks.map(this::convertToDetailDTO);
    }

    @Override
    public Page<TaskDetailDTO> getDepartmentTasks(Long departmentId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByDepartmentId(departmentId, pageable);
        return tasks.map(this::convertToDetailDTO);
    }

    @Override
    public Map<String, Long> getTaskStats(Long userId) {
        Map<String, Long> stats = new HashMap<>();
        for (Task.TaskStatus status : Task.TaskStatus.values()) {
            long count = taskRepository.countByAssigneeIdAndStatus(userId, status);
            stats.put(status.name(), count);
        }
        return stats;
    }

    @Override
    public Map<String, Long> getDepartmentTaskStats(Long departmentId) {
        Map<String, Long> stats = new HashMap<>();
        for (Task.TaskStatus status : Task.TaskStatus.values()) {
            long count = taskRepository.countByDepartmentIdAndStatus(departmentId, status);
            stats.put(status.name(), count);
        }
        return stats;
    }

    @Override
    public Task changeStatus(Long taskId, Task.TaskStatus newStatus, Long operatorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 权限检查：只有负责人或创建人可以修改状态
        if (!operatorId.equals(task.getReporter().getId()) &&
                (task.getAssignee() == null || !operatorId.equals(task.getAssignee().getId()))) {
            throw new RuntimeException("无权修改此任务状态");
        }

        // 状态流转验证（可选：添加业务规则）
        if (task.getStatus() == Task.TaskStatus.DONE && newStatus != Task.TaskStatus.DONE) {
            throw new RuntimeException("已完成的任务不能重新打开");
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    @Override
    public Task reassignTask(Long taskId, Long newAssigneeId, Long operatorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 权限检查：只有创建人或部门管理员可以重新分配
        if (!operatorId.equals(task.getReporter().getId())) {
            // 这里可以添加部门管理员检查逻辑
            throw new RuntimeException("无权重新分配此任务");
        }

        User newAssignee = userRepository.findById(newAssigneeId)
                .orElseThrow(() -> new RuntimeException("新负责人不存在"));
        task.setAssignee(newAssignee);

        return taskRepository.save(task);
    }

    // ---------- 私有辅助方法 ----------
    private TaskDetailDTO convertToDetailDTO(Task task) {
        TaskDetailDTO dto = new TaskDetailDTO();
        BeanUtils.copyProperties(task, dto);

        // 设置负责人信息
        if (task.getAssignee() != null) {
            TaskDetailDTO.SimpleUserDTO assigneeDTO = new TaskDetailDTO.SimpleUserDTO();
            BeanUtils.copyProperties(task.getAssignee(), assigneeDTO);
            dto.setAssignee(assigneeDTO);
        }

        // 设置创建人信息
        if (task.getReporter() != null) {
            TaskDetailDTO.SimpleUserDTO reporterDTO = new TaskDetailDTO.SimpleUserDTO();
            BeanUtils.copyProperties(task.getReporter(), reporterDTO);
            dto.setReporter(reporterDTO);
        }

        // 设置部门信息
        if (task.getDepartment() != null) {
            TaskDetailDTO.SimpleDeptDTO deptDTO = new TaskDetailDTO.SimpleDeptDTO();
            BeanUtils.copyProperties(task.getDepartment(), deptDTO);
            dto.setDepartment(deptDTO);
        }

        return dto;
    }
}