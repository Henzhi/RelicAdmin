package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackupRecord {
    private Long id;
    private String backupName;
    private String backupType;
    private String scope;
    private Long fileSize;
    private String filePath;
    private Integer operatorId;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
}
