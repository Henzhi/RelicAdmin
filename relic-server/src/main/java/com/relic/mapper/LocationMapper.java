package com.relic.mapper;

import com.relic.entity.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocationMapper {
    List<Location> selectAll();
    List<Location> selectByParentId(@Param("parentId") Integer parentId);
    List<Location> selectByType(@Param("type") String type, @Param("parentId") Integer parentId);
    Location selectById(@Param("id") Integer id);
    int insert(Location location);
    int update(Location location);
    int deleteById(@Param("id") Integer id);
}