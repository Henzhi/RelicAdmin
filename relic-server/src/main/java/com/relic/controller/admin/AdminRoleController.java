package com.relic.controller.admin;

import com.relic.dto.PageDTO;
import com.relic.dto.PermissionAssignDTO;
import com.relic.entity.Permission;
import com.relic.entity.Role;
import com.relic.result.Result;
import com.relic.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
@Tag(name = "管理端角色管理", description = "角色CRUD和权限分配")
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping("/page")
    public Result<PageDTO<Role>> page(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) String name) {
        return Result.success(roleService.page(name, page, pageSize));
    }

    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(roleService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Role role) {
        roleService.create(role);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Role role) {
        roleService.update(id, role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return Result.success();
    }

    @PutMapping("/{roleId}/permissions")
    public Result<Void> assignPermissions(@PathVariable Integer roleId,
                                           @RequestBody PermissionAssignDTO dto) {
        roleService.assignPermissions(roleId, dto.getPermissionIds());
        return Result.success();
    }

    @GetMapping("/{roleId}/permissions")
    public Result<List<Permission>> getPermissions(@PathVariable Integer roleId) {
        return Result.success(roleService.getRolePermissions(roleId));
    }
}