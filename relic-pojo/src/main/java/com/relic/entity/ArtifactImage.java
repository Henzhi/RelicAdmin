package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactImage {
    private Integer id;
    private Integer artifactId;
    private String imageUrl;
    private String imagePath;
    private Integer isPrimary;
    private Integer sortOrder;
}
