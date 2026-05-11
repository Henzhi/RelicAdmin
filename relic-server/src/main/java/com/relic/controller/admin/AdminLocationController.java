package com.relic.controller.admin;

import com.relic.dto.LocationCreateDTO;
import com.relic.dto.LocationUpdateDTO;
import com.relic.result.Result;
import com.relic.service.LocationService;
import com.relic.vo.LocationVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/location")
@RequiredArgsConstructor
@Tag(name = "管理端地点管理", description = "地点CRUD + 树形结构")
public class AdminLocationController {

    private final LocationService locationService;

    @GetMapping("/tree")
    public Result<List<LocationVO>> tree() {
        return Result.success(locationService.tree());
    }

    @GetMapping("/list")
    public Result<List<LocationVO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer parentId) {
        return Result.success(locationService.list(type, parentId));
    }

    @PostMapping
    public Result<Void> create(@RequestBody LocationCreateDTO dto) {
        locationService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody LocationUpdateDTO dto) {
        locationService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        locationService.delete(id);
        return Result.success();
    }
}