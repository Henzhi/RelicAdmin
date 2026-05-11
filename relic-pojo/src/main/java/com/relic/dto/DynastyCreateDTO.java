package com.relic.dto;

import lombok.Data;

@Data
public class DynastyCreateDTO {
    private String nameZh;
    private String nameEn;
    private Integer startYear;
    private Integer endYear;
    private String description;
}