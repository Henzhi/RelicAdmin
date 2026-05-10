package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dynasty {
    private Integer id;
    private String nameZh;
    private String nameEn;
    private Integer startYear;
    private Integer endYear;
    private String description;
    private LocalDateTime createdAt;
}
