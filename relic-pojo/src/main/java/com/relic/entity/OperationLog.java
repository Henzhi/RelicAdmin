package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationLog {
    private Long id;
    private Integer userId;
    private String operationType;
    private String targetType;
    private String targetId;
    private String oldValue;
    private String newValue;
    private String ip;
    private String userAgent;
    private LocalDateTime createdAt;
}
