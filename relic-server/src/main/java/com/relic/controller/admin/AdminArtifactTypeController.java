package com.relic.controller.admin;

import com.relic.dto.ArtifactTypeCreateDTO;
import com.relic.result.Result;
import com.relic.service.ArtifactTypeService;
import com.relic.vo.ArtifactTypeVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/artifact-type")
@RequiredArgsConstructor
@Tag(name = "管理端文物类型管理", description = "文物类型CRUD")
public class AdminArtifactTypeController {

    private final ArtifactTypeService artifactTypeService;

    @GetMapping("/page")
    public Result<PageResultVO<ArtifactTypeVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name) {
        return Result.success(artifactTypeService.page(name, page, pageSize));
    }

    @GetMapping("/list")
    public Result<List<ArtifactTypeVO>> list() {
        return Result.success(artifactTypeService.listAll());
    }

    @PostMapping
    public Result<Void> create(@RequestBody ArtifactTypeCreateDTO dto) {
        artifactTypeService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody ArtifactTypeCreateDTO dto) {
        artifactTypeService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        artifactTypeService.delete(id);
        return Result.success();
    }
}