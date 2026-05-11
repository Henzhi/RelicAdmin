package com.relic.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactVO {
    private Integer id;
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
    private String imagePath;
    private String creditLine;
    private String accessionNumber;
    private LocalDate crawlDate;
    private Integer imageValidated;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
}