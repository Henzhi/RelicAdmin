package com.relic.service.impl;

import com.relic.entity.ArtifactLike;
import com.relic.entity.CommentLike;
import com.relic.entity.PostLike;
import com.relic.mapper.ArtifactLikeMapper;
import com.relic.mapper.CommentLikeMapper;
import com.relic.mapper.PostLikeMapper;
import com.relic.service.LikeService;
import com.relic.vo.LikeCheckVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final ArtifactLikeMapper artifactLikeMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final PostLikeMapper postLikeMapper;

    @Override
    @Transactional
    public LikeCheckVO likeArtifact(Integer userId, Integer artifactId) {
        ArtifactLike exist = artifactLikeMapper.selectByUserAndArtifact(userId, artifactId);
        if (exist != null) {
            throw new IllegalArgumentException("已经点赞过该文物");
        }
        ArtifactLike like = new ArtifactLike();
        like.setUserId(userId);
        like.setArtifactId(artifactId);
        like.setLikedAt(LocalDateTime.now());
        artifactLikeMapper.insert(like);
        long count = artifactLikeMapper.countByArtifactId(artifactId);
        return LikeCheckVO.builder().liked(true).likeCount(count).build();
    }

    @Override
    @Transactional
    public LikeCheckVO unlikeArtifact(Integer userId, Integer artifactId) {
        artifactLikeMapper.delete(userId, artifactId);
        long count = artifactLikeMapper.countByArtifactId(artifactId);
        return LikeCheckVO.builder().liked(false).likeCount(count).build();
    }

    @Override
    public LikeCheckVO checkArtifactLike(Integer userId, Integer artifactId) {
        ArtifactLike exist = artifactLikeMapper.selectByUserAndArtifact(userId, artifactId);
        long count = artifactLikeMapper.countByArtifactId(artifactId);
        return LikeCheckVO.builder().liked(exist != null).likeCount(count).build();
    }

    @Override
    @Transactional
    public LikeCheckVO likeComment(Integer userId, Integer commentId) {
        CommentLike exist = commentLikeMapper.selectByUserAndComment(userId, commentId);
        if (exist != null) {
            throw new IllegalArgumentException("已经点赞过该评论");
        }
        CommentLike like = new CommentLike();
        like.setUserId(userId);
        like.setCommentId(commentId);
        like.setLikedAt(LocalDateTime.now());
        commentLikeMapper.insert(like);
        long count = commentLikeMapper.countByCommentId(commentId);
        return LikeCheckVO.builder().liked(true).likeCount(count).build();
    }

    @Override
    @Transactional
    public LikeCheckVO unlikeComment(Integer userId, Integer commentId) {
        commentLikeMapper.delete(userId, commentId);
        long count = commentLikeMapper.countByCommentId(commentId);
        return LikeCheckVO.builder().liked(false).likeCount(count).build();
    }

    @Override
    @Transactional
    public LikeCheckVO likePost(Integer userId, Integer postId) {
        PostLike exist = postLikeMapper.selectByUserAndPost(userId, postId);
        if (exist != null) {
            throw new IllegalArgumentException("已经点赞过该动态");
        }
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setPostId(postId);
        like.setLikedAt(LocalDateTime.now());
        postLikeMapper.insert(like);
        long count = postLikeMapper.countByPostId(postId);
        return LikeCheckVO.builder().liked(true).likeCount(count).build();
    }

    @Override
    @Transactional
    public LikeCheckVO unlikePost(Integer userId, Integer postId) {
        postLikeMapper.delete(userId, postId);
        long count = postLikeMapper.countByPostId(postId);
        return LikeCheckVO.builder().liked(false).likeCount(count).build();
    }
}