package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    private Integer id;
    private String nameZh;
    private String nameEn;
    private Integer birthYear;
    private Integer deathYear;
    private Integer dynastyId;
    private String biography;
    private String baiduUrl;
    private String wikiUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
