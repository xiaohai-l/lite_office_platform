package io.github.xiaohai.lite_office_platform.dto;

import io.github.xiaohai.lite_office_platform.entity.Task;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDetailDTO {
    private Long id;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private LocalDate dueDate;
    private Integer estimatedHours;

    // 关联对象信息
    private SimpleUserDTO assignee;      // 负责人
    private SimpleUserDTO reporter;      // 创建人
    private SimpleDeptDTO department;    // 所属部门

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 内嵌的简单DTO
    @Data
    public static class SimpleUserDTO {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
    }

    @Data
    public static class SimpleDeptDTO {
        private Long id;
        private String name;
    }
}