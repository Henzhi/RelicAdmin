package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private String position;
    private String targetAudience;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private Integer createdBy;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
