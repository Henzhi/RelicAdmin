package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String avatarUrl;
    private String nickname;
    private String status;
    private String banReason;
    private String userType;
    private LocalDateTime registeredAt;
    private LocalDateTime lastLogin;
    private String lastIp;
    private String source;
    private Integer commentDisabled;
    private Integer uploadDisabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
