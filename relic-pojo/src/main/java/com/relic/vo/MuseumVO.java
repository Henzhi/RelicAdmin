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
public class MuseumVO {
    private Integer id;
    private String name;
    private String shortName;
    private String country;
    private String city;
    private String website;
    private String collectionUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}