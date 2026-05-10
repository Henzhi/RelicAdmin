package com.relic.controller.admin;

import com.relic.dto.CommentUploadDisableDTO;
import com.relic.dto.PageDTO;
import com.relic.dto.RoleAssignDTO;
import com.relic.dto.UserBanDTO;
import com.relic.entity.User;
import com.relic.result.Result;
import com.relic.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "管理端用户管理", description = "用户列表、封禁、角色分配")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/page")
    public Result<PageDTO<User>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String status) {
        return Result.success(userService.page(username, nickname, status, page, pageSize));
    }

    @PutMapping("/ban/{userId}")
    public Result<Void> ban(@PathVariable Integer userId, @RequestBody UserBanDTO dto) {
        userService.ban(userId, dto);
        return Result.success();
    }

    @PutMapping("/{userId}/roles")
    public Result<Void> assignRoles(@PathVariable Integer userId, @RequestBody RoleAssignDTO dto) {
        userService.assignRoles(userId, dto.getRoleIds());
        return Result.success();
    }

    @PutMapping("/comment-disable/{userId}")
    public Result<Void> disableComment(@PathVariable Integer userId, @RequestBody CommentUploadDisableDTO dto) {
        userService.disableComment(userId, dto.getCommentDisabled());
        return Result.success();
    }

    @PutMapping("/upload-disable/{userId}")
    public Result<Void> disableUpload(@PathVariable Integer userId, @RequestBody CommentUploadDisableDTO dto) {
        userService.disableUpload(userId, dto.getUploadDisabled());
        return Result.success();
    }
}