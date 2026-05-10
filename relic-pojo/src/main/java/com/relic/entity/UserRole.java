package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    private Integer userId;
    private Integer roleId;
    private Integer grantedBy;
    private LocalDateTime grantedAt;
}
