package com.relic.dto;

import lombok.Data;

@Data
public class ViolationTypeUpdateDTO {
    private String typeName;
    private Integer severityLevel;
    private String defaultPenalty;
    private String description;
    private Integer status;
}