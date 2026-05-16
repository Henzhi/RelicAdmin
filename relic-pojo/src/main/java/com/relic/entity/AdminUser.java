package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUser {
    private Integer id;
    private String username;
    private String passwordHash;
    private String realName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String status;
    private LocalDateTime lastLogin;
    private String lastIp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}