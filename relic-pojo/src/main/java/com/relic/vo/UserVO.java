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
public class UserVO {
    private Integer id;
    private String username;
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