package com.relic.controller.admin;

import com.relic.context.BaseContext;
import com.relic.entity.UserComment;
import com.relic.result.Result;
import com.relic.service.CommentService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/comment")
@RequiredArgsConstructor
@Tag(name = "管理端-评论管理", description = "评论列表、审核、删除")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/page")
    public Result<PageResultVO<UserComment>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer artifactId) {
        return Result.success(commentService.page(username, status, artifactId, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<UserComment> getById(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id) {
        commentService.approve(id, BaseContext.getCurrentId().intValue());
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        commentService.reject(id, BaseContext.getCurrentId().intValue(), body.get("reason"));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        commentService.delete(id);
        return Result.success();
    }
}