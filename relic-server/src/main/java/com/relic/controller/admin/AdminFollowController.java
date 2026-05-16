package com.relic.controller.admin;

import com.relic.entity.UserFollow;
import com.relic.result.Result;
import com.relic.service.FollowService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/follow")
@RequiredArgsConstructor
@Tag(name = "管理端-用户关注管理", description = "关注关系列表、删除")
public class AdminFollowController {

    private final FollowService followService;

    @GetMapping("/page")
    public Result<PageResultVO<UserFollow>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String followerName,
            @RequestParam(required = false) String followeeName) {
        return Result.success(followService.page(followerName, followeeName, page, pageSize));
    }

    @DeleteMapping("/{followerId}/{followeeId}")
    public Result<Void> delete(@PathVariable Integer followerId, @PathVariable Integer followeeId) {
        followService.unfollow(followerId, followeeId);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Map<String, int[][]> body) {
        int[][] pairs = body.get("pairs");
        if (pairs != null) {
            for (int[] pair : pairs) {
                followService.unfollow(pair[0], pair[1]);
            }
        }
        return Result.success();
    }
}