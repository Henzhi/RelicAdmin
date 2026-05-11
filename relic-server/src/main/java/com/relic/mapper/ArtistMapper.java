package com.relic.mapper;

import com.relic.entity.Artist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtistMapper {
    Artist selectById(@Param("id") Integer id);
    List<Artist> selectByPage(@Param("nameZh") String nameZh, @Param("nameEn") String nameEn,
                              @Param("dynastyId") Integer dynastyId,
                              @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("nameZh") String nameZh, @Param("nameEn") String nameEn,
                     @Param("dynastyId") Integer dynastyId);
    int insert(Artist artist);
    int update(Artist artist);
    int deleteById(@Param("id") Integer id);
}