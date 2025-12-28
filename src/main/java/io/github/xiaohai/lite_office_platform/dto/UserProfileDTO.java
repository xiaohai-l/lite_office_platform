package io.github.xiaohai.lite_office_platform.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户个人信息响应DTO
 */
@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private LocalDateTime createTime;

    // 可以根据需要添加更多字段，如：部门名称、角色列表等
}