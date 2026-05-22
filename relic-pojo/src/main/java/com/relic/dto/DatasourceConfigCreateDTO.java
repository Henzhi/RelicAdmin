package com.relic.dto;

import lombok.Data;

@Data
public class DatasourceConfigCreateDTO {
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
}