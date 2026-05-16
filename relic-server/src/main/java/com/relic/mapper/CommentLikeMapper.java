package com.relic.mapper;

import com.relic.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentLikeMapper {
    void insert(CommentLike like);
    void delete(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    CommentLike selectByUserAndComment(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    long countByCommentId(@Param("commentId") Integer commentId);
}