package com.relic.dto;

import lombok.Data;

@Data
public class AnnouncementCreateDTO {
    private String title;
    private String content;
    private String position;
    private String targetAudience;
    private String startTime;
    private String endTime;
    private Integer status;
}