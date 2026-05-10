package com.relic.dto;

import lombok.Data;

@Data
public class PermissionCreateDTO {
    private String name;
    private String displayName;
    private String module;
}