package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppealRecord {
    private Long id;
    private Long penaltyId;
    private Integer userId;
    private String appealReason;
    private String evidence;
    private String status;
    private String reviewResult;
    private Integer reviewerId;
    private LocalDateTime reviewTime;
    private String reviewRemark;
    private LocalDateTime createdAt;
}
