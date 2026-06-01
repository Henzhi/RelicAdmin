package com.relic.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserVO {
    private Integer id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String status;
    private LocalDateTime lastLogin;
    private String lastIp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //角色id
    private Integer roleId;
}