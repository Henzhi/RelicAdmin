package com.relic.dto;

import lombok.Data;

@Data
public class AdminUserCreateDTO {
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private Integer[] roleIds;
}