package com.relic.dto;

import lombok.Data;

@Data
public class RestoreConfirmDTO {
    private Long backupId;
    private String confirmPassword;
}