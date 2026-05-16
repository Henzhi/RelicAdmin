package com.relic.dto;

import lombok.Data;

@Data
public class ContentSubmitDTO {
    private String contentId;
    private String contentType;
    private String content;
    private Integer submitterId;
}