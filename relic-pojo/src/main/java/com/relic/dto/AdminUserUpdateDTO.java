package com.relic.dto;

import lombok.Data;

@Data
public class AdminUserUpdateDTO {
    private String realName;
    private String email;
    private String phone;
    private String avatarUrl;
}