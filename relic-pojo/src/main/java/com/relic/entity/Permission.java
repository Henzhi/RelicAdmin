package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    private Integer id;
    private String name;
    private String displayName;
    private String module;
    private LocalDateTime createdAt;
}
