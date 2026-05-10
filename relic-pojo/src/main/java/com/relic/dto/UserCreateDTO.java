package com.relic.dto;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String source;
}