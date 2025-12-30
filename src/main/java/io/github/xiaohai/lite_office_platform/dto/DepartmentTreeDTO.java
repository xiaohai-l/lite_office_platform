package io.github.xiaohai.lite_office_platform.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
//返回树形结构用
@Data
public class DepartmentTreeDTO {
    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Long parentId;
    private List<DepartmentTreeDTO> children = new ArrayList<>(); // 嵌套的子部门

    // 可以添加其他业务字段，如：用户数量
    private Integer userCount = 0;
}