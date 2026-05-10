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
    private Integer museumId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer itemsCrawled;
    private Integer itemsNew;
    private Integer itemsUpdated;
    private String errorMessage;
}
