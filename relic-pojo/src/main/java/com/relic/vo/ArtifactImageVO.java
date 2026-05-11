package com.relic.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactImageVO {
    private Integer id;
    private Integer artifactId;
    private String imageUrl;
    private String imagePath;
    private Integer isPrimary;
    private Integer sortOrder;
}