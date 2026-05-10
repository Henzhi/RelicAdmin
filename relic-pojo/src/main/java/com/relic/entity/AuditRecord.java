package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditRecord {
    private Long id;
    private String contentId;
    private String contentType;
    private String sourceType;
    private String content;
    private Integer submitterId;
    private String autoAuditResult;
    private String manualAuditResult;
    private Integer auditorId;
    private LocalDateTime auditTime;
    private String rejectReason;
    private LocalDateTime createdAt;
}
