package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatasourceConfig {
    private Integer id;
    private String dsName;
    private String dsKey;
    private String dsType;
    private String host;
    private Integer port;
    private String databaseName;
    private String username;
    private String passwordEncrypted;
    private String extraParams;
    private Integer maxPoolSize;
    private String status;
    private LocalDateTime lastTestTime;
    private String lastTestResult;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}