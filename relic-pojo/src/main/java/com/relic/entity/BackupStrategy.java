package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackupStrategy {
    private Integer id;
    private Integer autoBackupEnabled;
    private String backupCron;
    private String backupType;
    private Integer retentionDays;
    private Integer encryptEnabled;
    private Integer notifyOnFailure;
    private String storagePath;
    private Long storageWarningThreshold;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}