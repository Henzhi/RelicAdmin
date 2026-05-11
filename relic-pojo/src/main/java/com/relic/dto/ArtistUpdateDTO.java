package com.relic.dto;

import lombok.Data;

@Data
public class ArtistUpdateDTO {
    private String nameZh;
    private String nameEn;
    private Integer birthYear;
    private Integer deathYear;
    private Integer dynastyId;
    private String biography;
    private String baiduUrl;
    private String wikiUrl;
}