package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationMapper {
    List<Map<String, Object>> selectByUserId(@Param("userId") Integer userId,
                                             @Param("isRead") Integer isRead,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    long countByUserId(@Param("userId") Integer userId, @Param("isRead") Integer isRead);

    int markReadByIds(@Param("userId") Integer userId, @Param("ids") Long[] ids);

    int markAllRead(@Param("userId") Integer userId);

    long countUnread(@Param("userId") Integer userId);

    int insert(@Param("userId") Integer userId,
               @Param("type") String type,
               @Param("title") String title,
               @Param("content") String content,
               @Param("extraData") String extraData);
}