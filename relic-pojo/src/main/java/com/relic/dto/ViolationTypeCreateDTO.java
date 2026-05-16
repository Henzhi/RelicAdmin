package com.relic.dto;

import lombok.Data;

@Data
public class ViolationTypeCreateDTO {
    private String typeCode;
    private String typeName;
    private Integer severityLevel;
    private String defaultPenalty;
    private String description;
}