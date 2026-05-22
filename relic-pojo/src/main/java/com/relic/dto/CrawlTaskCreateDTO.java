package com.relic.dto;

import lombok.Data;

@Data
public class CrawlTaskCreateDTO {
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
    private String description;
    private Integer enabled;
}