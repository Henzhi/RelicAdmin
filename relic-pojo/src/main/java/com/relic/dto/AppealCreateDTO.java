package com.relic.dto;

import lombok.Data;

@Data
public class AppealCreateDTO {
    private Long penaltyId;
    private String appealReason;
    private String evidence;
}