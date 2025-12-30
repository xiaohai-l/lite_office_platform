package io.github.xiaohai.lite_office_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
//常见和更新department使用
@Data
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    private String name;

    private String description;
    private Integer sortOrder;
    private Long parentId; // 父部门ID（创建时使用）
}