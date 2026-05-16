package com.relic.dto;

import lombok.Data;

@Data
public class PenaltyCreateDTO {
    private Integer userId;
    private String penaltyType;
    private String reason;
    private String expireTime;
    private String remark;
}