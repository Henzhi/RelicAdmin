package com.relic.dto;

import lombok.Data;

@Data
public class BackupCreateDTO {
    private String backupName;
    private String backupType;
    private String scope;
}