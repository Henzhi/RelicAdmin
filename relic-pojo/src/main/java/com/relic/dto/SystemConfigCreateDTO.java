package com.relic.dto;

import lombok.Data;

@Data
public class SystemConfigCreateDTO {
    private String configKey;
    private String configName;
    private String configValue;
    private String configType;
    private String configGroup;
    private String description;
    private Integer sortOrder;
}