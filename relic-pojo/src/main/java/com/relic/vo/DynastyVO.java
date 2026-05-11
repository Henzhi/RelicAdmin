package com.relic.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynastyVO {
    private Integer id;
    private String nameZh;
    private String nameEn;
    private Integer startYear;
    private Integer endYear;
    private String description;
    private LocalDateTime createdAt;
}