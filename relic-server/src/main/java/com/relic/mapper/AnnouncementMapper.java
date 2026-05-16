package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnouncementMapper {
    List<Map<String, Object>> selectAdminPage(@Param("status") Integer status,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    long countAdminPage(@Param("status") Integer status);

    List<Map<String, Object>> selectActiveList(@Param("position") String position);

    int insert(@Param("title") String title,
               @Param("content") String content,
               @Param("position") String position,
               @Param("targetAudience") String targetAudience,
               @Param("startTime") String startTime,
               @Param("endTime") String endTime,
               @Param("status") Integer status,
               @Param("createdBy") Long createdBy);

    int update(@Param("id") Long id,
               @Param("title") String title,
               @Param("content") String content,
               @Param("position") String position,
               @Param("targetAudience") String targetAudience,
               @Param("startTime") String startTime,
               @Param("endTime") String endTime,
               @Param("status") Integer status);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(@Param("id") Long id);
}