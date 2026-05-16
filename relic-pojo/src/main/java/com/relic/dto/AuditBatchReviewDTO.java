package com.relic.dto;

import lombok.Data;

@Data
public class AuditBatchReviewDTO {
    private Long[] ids;
    private String manualAuditResult;
    private String rejectReason;
}