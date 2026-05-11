package com.relic.mapper;

import com.relic.entity.Dynasty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DynastyMapper {
    Dynasty selectById(@Param("id") Integer id);
    List<Dynasty> selectAll();
    List<Dynasty> selectByPage(@Param("nameZh") String nameZh,
                               @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("nameZh") String nameZh);
    int insert(Dynasty dynasty);
    int update(Dynasty dynasty);
    int deleteById(@Param("id") Integer id);
}