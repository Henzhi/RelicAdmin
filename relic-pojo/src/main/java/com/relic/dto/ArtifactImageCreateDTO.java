package com.relic.dto;

import lombok.Data;

@Data
public class ArtifactImageCreateDTO {
    private String imageUrl;
    private String imagePath;
    private Integer isPrimary;
    private Integer sortOrder;
}