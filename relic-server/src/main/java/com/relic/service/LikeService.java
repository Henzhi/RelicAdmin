package com.relic.service;

import com.relic.vo.LikeCheckVO;

public interface LikeService {
    LikeCheckVO likeArtifact(Integer userId, Integer artifactId);
    LikeCheckVO unlikeArtifact(Integer userId, Integer artifactId);
    LikeCheckVO checkArtifactLike(Integer userId, Integer artifactId);
    LikeCheckVO likeComment(Integer userId, Integer commentId);
    LikeCheckVO unlikeComment(Integer userId, Integer commentId);
    LikeCheckVO likePost(Integer userId, Integer postId);
    LikeCheckVO unlikePost(Integer userId, Integer postId);
}