package com.relic.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ArtifactCreateDTO {
    private String objectId;
    private String titleZh;
    private String titleEn;
    private String timePeriod;
    private Integer dynastyId;
    private String type;
    private String material;
    private String description;
    private String dimensions;
    private Integer museumId;
    private Integer locationId;
    private String detailUrl;
    private String imageUrl;
    private String creditLine;
    private String accessionNumber;
    private LocalDate crawlDate;
}