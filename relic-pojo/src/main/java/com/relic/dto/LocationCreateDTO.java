package com.relic.dto;

import lombok.Data;

@Data
public class LocationCreateDTO {
    private String nameZh;
    private String nameEn;
    private Integer parentId;
    private String type;
}