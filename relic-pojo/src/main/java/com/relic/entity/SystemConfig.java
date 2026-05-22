package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfig {
    private Integer id;
    private String configKey;
    private String configName;
    private String configValue;
    private String configType;
    private String configGroup;
    private String description;
    private Integer sortOrder;
    private Integer editable;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}