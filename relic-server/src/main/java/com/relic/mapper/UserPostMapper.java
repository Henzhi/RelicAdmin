package com.relic.mapper;

import com.relic.entity.UserPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserPostMapper {
    UserPost selectById(@Param("id") Integer id);
    List<Map<String, Object>> selectByPage(@Param("title") String title,
                                           @Param("userId") Integer userId,
                                           @Param("username") String username,
                                           @Param("status") String status,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    long countByPage(@Param("title") String title,
                     @Param("userId") Integer userId,
                     @Param("username") String username,
                     @Param("status") String status,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);
    /**
     * 查询尚未同步到 user_behaviors 的发布记录
     */
    List<Map<String, Object>> selectUnsynced(@Param("limit") int limit);

    List<Map<String, Object>> selectUnaudited(@Param("limit") int limit);

    int updateAuditStatus(@Param("id") Integer id, @Param("status") String status,
                          @Param("auditBy") Integer auditBy, @Param("rejectReason") String rejectReason);
}