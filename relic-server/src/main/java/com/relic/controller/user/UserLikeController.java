package com.relic.controller.user;

import com.relic.context.BaseContext;
import com.relic.result.Result;
import com.relic.service.LikeService;
import com.relic.vo.LikeCheckVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/like")
@RequiredArgsConstructor
@Tag(name = "用户点赞模块", description = "点赞文物/评论/动态")
public class UserLikeController {

    private final LikeService likeService;

    @PostMapping("/artifact/{artifactId}")
    public Result<LikeCheckVO> likeArtifact(@PathVariable Integer artifactId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(likeService.likeArtifact(userId.intValue(), artifactId));
    }

    @DeleteMapping("/artifact/{artifactId}")
    public Result<LikeCheckVO> unlikeArtifact(@PathVariable Integer artifactId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(likeService.unlikeArtifact(userId.intValue(), artifactId));
    }

    @PostMapping("/comment/{commentId}")
    public Result<LikeCheckVO> likeComment(@PathVariable Integer commentId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(likeService.likeComment(userId.intValue(), commentId));
    }

    @DeleteMapping("/comment/{commentId}")
    public Result<LikeCheckVO> unlikeComment(@PathVariable Integer commentId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(likeService.unlikeComment(userId.intValue(), commentId));
    }

    @PostMapping("/post/{postId}")
    public Result<LikeCheckVO> likePost(@PathVariable Integer postId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(likeService.likePost(userId.intValue(), postId));
    }

    @DeleteMapping("/post/{postId}")
    public Result<LikeCheckVO> unlikePost(@PathVariable Integer postId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(likeService.unlikePost(userId.intValue(), postId));
    }
}