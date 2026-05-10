package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactArtist {
    private Integer artifactId;
    private Integer artistId;
    private String relationshipType;
}
