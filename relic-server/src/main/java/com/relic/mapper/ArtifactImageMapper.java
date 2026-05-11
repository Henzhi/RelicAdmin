package com.relic.mapper;

import com.relic.entity.ArtifactImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtifactImageMapper {
    List<ArtifactImage> selectByArtifactId(@Param("artifactId") Integer artifactId);
    ArtifactImage selectById(@Param("id") Integer id);
    int insert(ArtifactImage image);
    int update(ArtifactImage image);
    int deleteById(@Param("id") Integer id);
    int clearPrimary(@Param("artifactId") Integer artifactId);
    int setPrimary(@Param("id") Integer id);
}