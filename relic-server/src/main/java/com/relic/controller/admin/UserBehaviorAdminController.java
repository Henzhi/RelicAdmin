package com.relic.controller.admin;

import com.relic.result.Result;
import com.relic.service.UserBehaviorAdminService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/user-behavior")
@RequiredArgsConstructor
@Tag(name = "管理端-用户行为记录", description = "查看全平台用户的收藏/喜欢/点赞/评论/动态/关注/上传/浏览/行为日志")
public class UserBehaviorAdminController {

    private final UserBehaviorAdminService userBehaviorAdminService;

    @GetMapping("/favorites/page")
    public Result<PageResultVO<Map<String, Object>>> listFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String artifactName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listFavorites(username, artifactName, startTime, endTime, page, pageSize));
    }

    @GetMapping("/artifact-likes/page")
    public Result<PageResultVO<Map<String, Object>>> listArtifactLikes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listArtifactLikes(userId, username, startTime, endTime, page, pageSize));
    }

    @GetMapping("/post-likes/page")
    public Result<PageResultVO<Map<String, Object>>> listPostLikes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listPostLikes(userId, username, startTime, endTime, page, pageSize));
    }

    @GetMapping("/comment-likes/page")
    public Result<PageResultVO<Map<String, Object>>> listCommentLikes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listCommentLikes(userId, username, startTime, endTime, page, pageSize));
    }

    @GetMapping("/posts/page")
    public Result<PageResultVO<Map<String, Object>>> listPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listPosts(title, userId, status, username, startTime, endTime, page, pageSize));
    }

    @GetMapping("/comments/page")
    public Result<PageResultVO<Map<String, Object>>> listComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer artifactId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listComments(keyword, userId, username, artifactId, status, startTime, endTime, page, pageSize));
    }

    @GetMapping("/follows/page")
    public Result<PageResultVO<Map<String, Object>>> listFollows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer followerId,
            @RequestParam(required = false) Integer followeeId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listFollows(followerId, followeeId, username, startTime, endTime, page, pageSize));
    }

    @GetMapping("/uploads/page")
    public Result<PageResultVO<Map<String, Object>>> listUploads(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String mediaType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listUploads(userId, mediaType, status, username, startTime, endTime, page, pageSize));
    }

    @GetMapping("/browse-history/page")
    public Result<PageResultVO<Map<String, Object>>> listBrowseHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer artifactId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listBrowseHistory(userId, username, artifactId, startTime, endTime, page, pageSize));
    }

    @GetMapping("/behavior-logs/page")
    public Result<PageResultVO<Map<String, Object>>> listBehaviorLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(userBehaviorAdminService.listBehaviorLogs(userId, behaviorType, keyword, username, startTime, endTime, page, pageSize));
    }
}