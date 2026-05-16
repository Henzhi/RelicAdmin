package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SensitiveWordMapper {
    List<Map<String, Object>> selectByPage(@Param("word") String word,
                                           @Param("category") String category,
                                           @Param("status") Integer status,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("word") String word,
                     @Param("category") String category,
                     @Param("status") Integer status);

    int insert(@Param("word") String word, @Param("category") String category);

    int checkExists(@Param("word") String word);

    int update(@Param("id") Long id, @Param("word") String word,
               @Param("category") String category, @Param("status") Integer status);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(@Param("id") Long id);

    int batchInsert(@Param("list") List<Map<String, String>> list);

    List<String> selectAllEnabledWords();
}