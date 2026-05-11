package com.relic.mapper;

import com.relic.entity.Museum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MuseumMapper {
    Museum selectById(@Param("id") Integer id);
    List<Museum> selectByPage(@Param("name") String name, @Param("country") String country,
                              @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("name") String name, @Param("country") String country);
    int insert(Museum museum);
    int update(Museum museum);
    int deleteById(@Param("id") Integer id);
}