package com.relic.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVO {
    private Integer id;
    private String name;
    private String displayName;
    private String module;
    private LocalDateTime createdAt;
}