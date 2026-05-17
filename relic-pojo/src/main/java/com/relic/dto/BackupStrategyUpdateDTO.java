package com.relic.dto;

import lombok.Data;

@Data
public class BackupStrategyUpdateDTO {
    private Integer autoBackupEnabled;
    private String backupCron;
    private String backupType;
    private Integer retentionDays;
    private Integer encryptEnabled;
    private Integer notifyOnFailure;
    private String storagePath;
    private Long storageWarningThreshold;
    private Integer status;
}