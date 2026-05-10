package com.relic.controller.admin;

import com.relic.dto.PageDTO;
import com.relic.entity.Permission;
import com.relic.result.Result;
import com.relic.service.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permission")
@RequiredArgsConstructor
@Tag(name = "管理端权限管理", description = "权限CRUD")
public class AdminPermissionController {

    private final PermissionService permissionService;

    @GetMapping("/page")
    public Result<PageDTO<Permission>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String module) {
        return Result.success(permissionService.page(name, module, page, pageSize));
    }

    @GetMapping("/list")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.listAll());
    }

    @PostMapping
    public Result<Void> create(@RequestBody Permission permission) {
        permissionService.create(permission);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Permission permission) {
        permissionService.update(id, permission);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return Result.success();
    }
}