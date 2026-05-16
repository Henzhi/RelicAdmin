package com.relic.controller.admin;

import com.relic.context.BaseContext;
import com.relic.entity.UserPost;
import com.relic.result.Result;
import com.relic.service.PostService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/post")
@RequiredArgsConstructor
@Tag(name = "管理端-用户动态管理", description = "用户动态列表、审核、删除")
public class AdminPostController {

    private final PostService postService;

    @GetMapping("/page")
    public Result<PageResultVO<UserPost>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {
        return Result.success(postService.page(username, status, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<UserPost> getById(@PathVariable Integer id) {
        return Result.success(postService.getById(id));
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id) {
        postService.approve(id, BaseContext.getCurrentId().intValue());
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        postService.reject(id, BaseContext.getCurrentId().intValue(), body.get("reason"));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        postService.delete(id);
        return Result.success();
    }
}