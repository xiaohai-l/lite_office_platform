package io.github.xiaohai.lite_office_platform.controller;

import io.github.xiaohai.lite_office_platform.common.R;
import io.github.xiaohai.lite_office_platform.common.SecurityUtil;
import io.github.xiaohai.lite_office_platform.dto.TaskDTO;
import io.github.xiaohai.lite_office_platform.dto.TaskDetailDTO;
import io.github.xiaohai.lite_office_platform.entity.Task;
import io.github.xiaohai.lite_office_platform.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SecurityUtil securityUtil;

    /** 创建任务 */
    @PostMapping
    @PreAuthorize("hasAuthority('task:create')")
    public R<Task> create(@Valid @RequestBody TaskDTO dto) {
        Long currentUserId = securityUtil.getCurrentUserId();
        Task task = taskService.createTask(dto, currentUserId);
        return R.success(task);
    }

    /** 更新任务 */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('task:edit')")
    public R<Task> update(@PathVariable Long id, @Valid @RequestBody TaskDTO dto) {
        Task task = taskService.updateTask(id, dto);
        return R.success(task);
    }

    /** 删除任务 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('task:delete')")
    public R<String> delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return R.success("删除成功");
    }

    /** 获取任务详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('task:view')")
    public R<TaskDetailDTO> getDetail(@PathVariable Long id) {
        TaskDetailDTO detail = taskService.getTaskDetail(id);
        return R.success(detail);
    }

    /** 综合搜索任务 */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('task:view')")
    public R<Page<TaskDetailDTO>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Task.TaskStatus status,
            @RequestParam(required = false) Task.TaskPriority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) LocalDate dueDateFrom,
            @RequestParam(required = false) LocalDate dueDateTo,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<TaskDetailDTO> result = taskService.searchTasks(
                title, status, priority, assigneeId, departmentId,
                dueDateFrom, dueDateTo, pageable);
        return R.success(result);
    }

    /** 获取我创建的任务 */
    @GetMapping("/my-created")
    @PreAuthorize("hasAuthority('task:view')")
    public R<Page<TaskDetailDTO>> getMyCreated(
            @PageableDefault(size = 20) Pageable pageable) {
        Long currentUserId = securityUtil.getCurrentUserId();
        Page<TaskDetailDTO> result = taskService.getMyTasks(currentUserId, pageable);
        return R.success(result);
    }

    /** 获取分配给我的任务 */
    @GetMapping("/my-assigned")
    @PreAuthorize("hasAuthority('task:view')")
    public R<Page<TaskDetailDTO>> getMyAssigned(
            @PageableDefault(size = 20) Pageable pageable) {
        Long currentUserId = securityUtil.getCurrentUserId();
        Page<TaskDetailDTO> result = taskService.getAssignedTasks(currentUserId, pageable);
        return R.success(result);
    }

    /** 获取部门任务 */
    @GetMapping("/department/{deptId}")
    @PreAuthorize("hasAuthority('task:view')")
    public R<Page<TaskDetailDTO>> getDepartmentTasks(
            @PathVariable Long deptId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskDetailDTO> result = taskService.getDepartmentTasks(deptId, pageable);
        return R.success(result);
    }

    /** 任务统计 */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('task:view')")
    public R<Map<String, Long>> getStats() {
        Long currentUserId = securityUtil.getCurrentUserId();
        Map<String, Long> stats = taskService.getTaskStats(currentUserId);
        return R.success(stats);
    }

    /** 修改任务状态 */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('task:edit')")
    public R<Task> changeStatus(
            @PathVariable Long id,
            @RequestParam Task.TaskStatus newStatus) {
        Long currentUserId = securityUtil.getCurrentUserId();
        Task task = taskService.changeStatus(id, newStatus, currentUserId);
        return R.success(task);
    }

    /** 重新分配任务 */
    @PatchMapping("/{id}/reassign")
    @PreAuthorize("hasAuthority('task:assign')")
    public R<Task> reassign(
            @PathVariable Long id,
            @RequestParam Long newAssigneeId) {
        Long currentUserId = securityUtil.getCurrentUserId();
        Task task = taskService.reassignTask(id, newAssigneeId, currentUserId);
        return R.success(task);
    }
}