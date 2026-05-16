package com.relic.dto;

import lombok.Data;

@Data
public class AuditReviewDTO {
    private String manualAuditResult;
    private String rejectReason;
}