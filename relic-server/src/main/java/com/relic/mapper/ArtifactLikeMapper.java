package com.relic.mapper;

import com.relic.entity.ArtifactLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArtifactLikeMapper {
    void insert(ArtifactLike like);
    void delete(@Param("userId") Integer userId, @Param("artifactId") Integer artifactId);
    ArtifactLike selectByUserAndArtifact(@Param("userId") Integer userId, @Param("artifactId") Integer artifactId);
    long countByArtifactId(@Param("artifactId") Integer artifactId);
    List<Map<String, Object>> selectAllByPage(@Param("userId") Integer userId,
                                              @Param("username") String username,
                                              @Param("startTime") String startTime,
                                              @Param("endTime") String endTime,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);
    long countAll(@Param("userId") Integer userId,
                  @Param("username") String username,
                  @Param("startTime") String startTime,
                  @Param("endTime") String endTime);
}