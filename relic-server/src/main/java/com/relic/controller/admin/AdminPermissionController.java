package com.relic.controller.admin;

import com.relic.dto.PermissionCreateDTO;
import com.relic.dto.PermissionUpdateDTO;
import com.relic.result.Result;
import com.relic.service.PermissionService;
import com.relic.vo.PageResultVO;
import com.relic.vo.PermissionVO;
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
    public Result<PageResultVO<PermissionVO>> page(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String module) {
        return Result.success(permissionService.page(name, module, page, pageSize));
    }

    @GetMapping("/list")
    public Result<List<PermissionVO>> list() {
        return Result.success(permissionService.listAll());
    }

    @PostMapping
    public Result<Void> create(@RequestBody PermissionCreateDTO dto) {
        permissionService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody PermissionUpdateDTO dto) {
        permissionService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return Result.success();
    }
}