package com.relic.mapper;

import com.relic.entity.Artifact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtifactMapper {
    Artifact selectById(@Param("id") Integer id);
    List<Artifact> selectByPage(@Param("titleZh") String titleZh, @Param("titleEn") String titleEn,
                                @Param("type") String type, @Param("dynastyId") Integer dynastyId,
                                @Param("museumId") Integer museumId, @Param("material") String material,
                                @Param("keyword") String keyword,
                                @Param("sortBy") String sortBy, @Param("sortOrder") String sortOrder,
                                @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("titleZh") String titleZh, @Param("titleEn") String titleEn,
                     @Param("type") String type, @Param("dynastyId") Integer dynastyId,
                     @Param("museumId") Integer museumId, @Param("material") String material,
                     @Param("keyword") String keyword);
    int insert(Artifact artifact);
    int update(Artifact artifact);
    int deleteById(@Param("id") Integer id);
}