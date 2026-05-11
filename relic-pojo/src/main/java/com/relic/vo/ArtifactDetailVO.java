package com.relic.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArtifactDetailVO extends ArtifactVO {
    private String dynastyName;
    private String museumName;
    private String locationName;
    private List<ArtifactImageVO> images;
    private List<ArtifactArtistVO> artists;
    private List<ArtifactLocationVO> relicLocations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtifactArtistVO {
        private Integer artistId;
        private String artistNameZh;
        private String relationshipType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtifactLocationVO {
        private Integer locationId;
        private String locationNameZh;
        private String role;
    }
}