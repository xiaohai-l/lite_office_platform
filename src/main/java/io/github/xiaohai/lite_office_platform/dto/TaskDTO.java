package io.github.xiaohai.lite_office_platform.dto;

import io.github.xiaohai.lite_office_platform.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskDTO {
    private Long id;

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "任务状态不能为空")
    private Task.TaskStatus status;

    @NotNull(message = "任务优先级不能为空")
    private Task.TaskPriority priority;

    private LocalDate dueDate;
    private Integer estimatedHours;
    private Long assigneeId;    // 负责人ID
    private Long departmentId;  // 部门ID
}