package com.relic.controller.admin;

import com.relic.dto.*;
import com.relic.result.Result;
import com.relic.service.UserService;
import com.relic.vo.PageResultVO;
import com.relic.vo.UserVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "管理端用户管理", description = "用户列表、新增、删除、封禁、角色分配")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/page")
    public Result<PageResultVO<UserVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userType) {
        return Result.success(userService.page(username, nickname, status, userType, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody UserCreateDTO dto) {
        userService.create(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.success();
    }

    @PutMapping("/ban/{userId}")
    public Result<Void> ban(@PathVariable Integer userId, @RequestBody UserBanDTO dto) {
        userService.ban(userId, dto);
        return Result.success();
    }

    //用户不应该有管理员角色分配（弃用）
//    @PutMapping("/{userId}/roles")
//    public Result<Void> assignRoles(@PathVariable Integer userId, @RequestBody RoleAssignDTO dto) {
//        userService.assignRoles(userId, dto.getRoleId());
//        return Result.success();
//    }

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