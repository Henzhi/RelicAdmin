package com.relic.controller.admin;

import com.relic.context.BaseContext;
import com.relic.dto.AdminUserCreateDTO;
import com.relic.dto.AdminUserUpdateDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RoleAssignDTO;
import com.relic.result.Result;
import com.relic.service.AdminUserService;
import com.relic.utils.AliOssUtil;
import com.relic.vo.AdminUserVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.InvalidRoleValueException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/admin-user")
@RequiredArgsConstructor
@Tag(name = "管理端-管理员账号管理", description = "管理员账号的增删改查、角色分配")
public class AdminAccountController {

    private final AdminUserService adminUserService;
    private final AliOssUtil aliOssUtil;

    @GetMapping("/page")
    public Result<PageResultVO<AdminUserVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String status) {
        PageResultVO<AdminUserVO> pageResultVO = adminUserService.page(username, realName, status, page, pageSize);
//        log.info("{}",pageResultVO);
        log.info("当前操作人Id：{}",BaseContext.getCurrentId());
        return Result.success(pageResultVO);
    }

    @GetMapping("/{id}")
    public Result<AdminUserVO> getById(@PathVariable Integer id) {
        return Result.success(adminUserService.getById(id));
    }

    @GetMapping("/current")
    public Result<AdminUserVO> getCurrentAdmin() {
        return Result.success(adminUserService.getCurrentAdmin());
    }

    @PostMapping
    public Result<Void> create(@RequestBody AdminUserCreateDTO dto) {
        adminUserService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody AdminUserUpdateDTO dto) {
        adminUserService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        adminUserService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        adminUserService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{adminUserId}/roles")
    public Result<Void> assignRoles(@PathVariable Integer adminUserId, @RequestBody RoleAssignDTO dto) throws InvalidRoleValueException {
        adminUserService.assignRoles(adminUserId, dto.getRoleId());
        return Result.success();
    }

    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(@PathVariable Integer id, @RequestBody PasswordChangeDTO dto) {
        adminUserService.updatePassword(id, dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        adminUserService.resetPassword(id, body.get("newPassword"));
        return Result.success();
    }

    @PutMapping("/current/profile")
    public Result<Void> updateCurrentProfile(@RequestBody AdminUserUpdateDTO dto) {
        Long currentId = BaseContext.getCurrentId();
        adminUserService.update(currentId.intValue(), dto);
        return Result.success();
    }

    @PutMapping("/current/password")
    public Result<Void> updateCurrentPassword(@RequestBody PasswordChangeDTO dto) {
        Long currentId = BaseContext.getCurrentId();
        adminUserService.updatePassword(currentId.intValue(), dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    @PostMapping("/current/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        Long currentId = BaseContext.getCurrentId();
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String objectName = "avatar/" + currentId + "/" + UUID.randomUUID() + ext;
        String avatarUrl = aliOssUtil.upload(file.getBytes(), objectName);
        return Result.success(avatarUrl);
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Map<String, Integer[]> body) {
        adminUserService.batchDelete(body.get("ids"));
        return Result.success();
    }
}