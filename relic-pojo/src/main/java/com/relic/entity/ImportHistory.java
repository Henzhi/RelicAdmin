package com.relic.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ImportHistory {
    private Integer id;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private String status;
    private String errorReport;
    private Integer operatorId;
    private String operatorName;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}
