package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditStrategy {
    private Integer id;
    private String contentType;
    private String autoMode;
    private Integer enableSensitiveCheck;
    private Integer enableImageCheck;
    private Integer riskThreshold;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}