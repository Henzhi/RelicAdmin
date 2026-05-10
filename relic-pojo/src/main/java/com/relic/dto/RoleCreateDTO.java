package com.relic.dto;

import lombok.Data;

@Data
public class RoleCreateDTO {
    private String name;
    private String displayName;
    private String description;
}