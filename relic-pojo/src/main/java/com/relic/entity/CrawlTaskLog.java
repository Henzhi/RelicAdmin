package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawlTaskLog {
    private Integer id;
    private Integer taskId;
    private String taskName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer crawledCount;
    private String errorMessage;
    private Integer retryCount;
    private Integer executorId;
    private LocalDateTime createdAt;
}