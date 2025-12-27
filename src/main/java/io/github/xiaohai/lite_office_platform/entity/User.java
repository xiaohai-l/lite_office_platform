package io.github.xiaohai.lite_office_platform.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
//import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
@Entity
@Table(name = "sys_user")
@Data
public class User {
    @Id//id作为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//主键自增
    private Long id;

    @Column(unique = true, nullable = false, length = 50) // 用户名 唯一，非空，长度50
    private String username;

    @Column(nullable = false) // 密码 非空
    private String password;

    @Column(nullable = false, length = 50) // 昵称，非空，长度50
    private String nickname;

    @Column(unique = true, length = 100) // 唯一，长度100
    private String email;

    private String avatar; // 头像URL，可以为空

    @CreationTimestamp //  Hibernate注解，在创建时自动设置为当前时间
    @Column(updatable = false) //  此字段在更新时不可修改
    private LocalDateTime createTime;

    @UpdateTimestamp //  Hibernate注解，在更新时自动设置为当前时间
    private LocalDateTime updateTime;


}
