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
public class ArtistVO {
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