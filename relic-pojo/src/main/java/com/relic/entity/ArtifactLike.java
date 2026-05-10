package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactLike {
    private Integer userId;
    private Integer artifactId;
    private LocalDateTime likedAt;
}
