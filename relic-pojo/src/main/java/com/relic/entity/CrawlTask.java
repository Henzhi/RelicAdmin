package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawlTask {
    private Integer id;
    private String taskName;
    private String taskCode;
    private String sourceUrl;
    private String sourceType;
    private String crawlRule;
    private Integer priority;
    private String cronExpression;
    private Integer maxRetry;
    private Integer retryDelay;
    private Integer timeoutSeconds;
    private String status;
    private LocalDateTime lastRunTime;
    private LocalDateTime nextRunTime;
    private Integer totalRuns;
    private Integer successRuns;
    private Integer failRuns;
    private String description;
    private Integer enabled;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}