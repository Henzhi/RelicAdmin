package com.relic.dto;

import lombok.Data;

@Data
public class AuditStrategyUpdateDTO {
    private String autoMode;
    private Integer enableSensitiveCheck;
    private Integer enableImageCheck;
    private Integer riskThreshold;
    private Integer status;
}