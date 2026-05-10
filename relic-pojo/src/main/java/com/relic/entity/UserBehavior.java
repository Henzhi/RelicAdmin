package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBehavior {
    private Long id;
    private Integer userId;
    private String behaviorType;
    private String targetType;
    private String targetId;
    private String targetDesc;
    private String ip;
    private String device;
    private String detail;
    private LocalDateTime createdAt;
}
