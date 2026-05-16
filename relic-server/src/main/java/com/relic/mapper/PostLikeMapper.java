package com.relic.mapper;

import com.relic.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeMapper {
    void insert(PostLike like);
    void delete(@Param("userId") Integer userId, @Param("postId") Integer postId);
    PostLike selectByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);
    long countByPostId(@Param("postId") Integer postId);
}