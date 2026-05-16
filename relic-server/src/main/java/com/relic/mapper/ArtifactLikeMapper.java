package com.relic.mapper;

import com.relic.entity.ArtifactLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArtifactLikeMapper {
    void insert(ArtifactLike like);
    void delete(@Param("userId") Integer userId, @Param("artifactId") Integer artifactId);
    ArtifactLike selectByUserAndArtifact(@Param("userId") Integer userId, @Param("artifactId") Integer artifactId);
    long countByArtifactId(@Param("artifactId") Integer artifactId);
}