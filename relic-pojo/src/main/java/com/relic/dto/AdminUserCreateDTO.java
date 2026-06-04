package com.relic.dto;

import lombok.Data;

@Data
public class AdminUserCreateDTO {
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    //已弃用多选，改为单选
    private Integer roleId;
}