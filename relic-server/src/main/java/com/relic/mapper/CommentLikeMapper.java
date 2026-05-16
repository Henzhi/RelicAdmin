package com.relic.mapper;

import com.relic.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentLikeMapper {
    void insert(CommentLike like);
    void delete(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    CommentLike selectByUserAndComment(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    long countByCommentId(@Param("commentId") Integer commentId);
    List<Map<String, Object>> selectAllByPage(@Param("userId") Integer userId,
                                              @Param("username") String username,
                                              @Param("startTime") String startTime,
                                              @Param("endTime") String endTime,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);
    long countAll(@Param("userId") Integer userId,
                  @Param("username") String username,
                  @Param("startTime") String startTime,
                  @Param("endTime") String endTime);
}