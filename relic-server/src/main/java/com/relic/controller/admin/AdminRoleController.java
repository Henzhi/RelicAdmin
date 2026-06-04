package com.relic.controller.admin;

import com.relic.dto.PermissionAssignDTO;
import com.relic.dto.RoleCreateDTO;
import com.relic.dto.RoleUpdateDTO;
import com.relic.result.Result;
import com.relic.service.RoleService;
import com.relic.vo.PageResultVO;
import com.relic.vo.PermissionVO;
import com.relic.vo.RoleVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
@Tag(name = "管理端角色管理", description = "角色CRUD和权限分配")
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping("/page")
    public Result<PageResultVO<RoleVO>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam(required = false) String name) {
        log.info("查询角色信息...");
        return Result.success(roleService.page(name, page, pageSize));
    }

    @GetMapping("/list")
    public Result<List<RoleVO>> list() {
        return Result.success(roleService.listAll());
    }

    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody RoleCreateDTO dto) {
        roleService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody RoleUpdateDTO dto) {
        roleService.update(id, dto);
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
    public Result<List<PermissionVO>> getPermissions(@PathVariable Integer roleId) {
        return Result.success(roleService.getRolePermissions(roleId));
    }
}