package com.relic.mapper;

import com.relic.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostLikeMapper {
    void insert(PostLike like);
    void delete(@Param("userId") Integer userId, @Param("postId") Integer postId);
    PostLike selectByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);
    long countByPostId(@Param("postId") Integer postId);
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