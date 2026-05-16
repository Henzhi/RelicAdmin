package com.relic.controller.admin;

import com.relic.dto.AdminUserCreateDTO;
import com.relic.dto.AdminUserUpdateDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RoleAssignDTO;
import com.relic.result.Result;
import com.relic.service.AdminUserService;
import com.relic.vo.AdminUserVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/admin-user")
@RequiredArgsConstructor
@Tag(name = "管理端-管理员账号管理", description = "管理员账号的增删改查、角色分配")
public class AdminAccountController {

    private final AdminUserService adminUserService;

    @GetMapping("/page")
    public Result<PageResultVO<AdminUserVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String status) {
        return Result.success(adminUserService.page(username, realName, status, page, pageSize));
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
    public Result<Void> assignRoles(@PathVariable Integer adminUserId, @RequestBody RoleAssignDTO dto) {
        adminUserService.assignRoles(adminUserId, dto.getRoleIds());
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

    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Map<String, Integer[]> body) {
        adminUserService.batchDelete(body.get("ids"));
        return Result.success();
    }
}