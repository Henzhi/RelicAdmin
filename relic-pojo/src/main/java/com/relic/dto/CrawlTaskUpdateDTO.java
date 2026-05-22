package com.relic.dto;

import lombok.Data;

@Data
public class CrawlTaskUpdateDTO {
    private String taskName;
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