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
    private String configValue;
    private String description;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
}
