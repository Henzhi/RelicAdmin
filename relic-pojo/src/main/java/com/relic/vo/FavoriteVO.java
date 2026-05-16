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
public class FavoriteVO {
    private Integer artifactId;
    private String titleZh;
    private String titleEn;
    private String imageUrl;
    private String groupName;
    private LocalDateTime createdAt;
}