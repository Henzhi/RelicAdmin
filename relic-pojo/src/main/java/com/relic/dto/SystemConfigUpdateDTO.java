package com.relic.dto;

import lombok.Data;

@Data
public class SystemConfigUpdateDTO {
    private String configName;
    private String configValue;
    private String configType;
    private String configGroup;
    private String description;
    private Integer sortOrder;
    private Integer status;
}