package com.relic.mapper;

import com.relic.entity.ArtifactType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtifactTypeMapper {
    List<ArtifactType> selectAll();
    ArtifactType selectById(@Param("id") Integer id);
    List<ArtifactType> selectByPage(@Param("name") String name, @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("name") String name);
    int insert(ArtifactType type);
    int update(ArtifactType type);
    int deleteById(@Param("id") Integer id);
}