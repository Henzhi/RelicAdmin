package com.relic.service;

import com.relic.dto.ArtifactImageCreateDTO;
import com.relic.vo.ArtifactImageVO;

import java.util.List;

public interface ArtifactImageService {
    List<ArtifactImageVO> getByArtifactId(Integer artifactId);
    void create(Integer artifactId, ArtifactImageCreateDTO dto);
    void update(Integer artifactId, Integer imageId, ArtifactImageCreateDTO dto);
    void delete(Integer artifactId, Integer imageId);
    void setPrimary(Integer artifactId, Integer imageId);
}