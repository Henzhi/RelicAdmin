package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyRecord {
    private Long id;
    private Integer userId;
    private String penaltyType;
    private String reason;
    private Integer operatorId;
    private LocalDateTime penaltyTime;
    private LocalDateTime expireTime;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
}
