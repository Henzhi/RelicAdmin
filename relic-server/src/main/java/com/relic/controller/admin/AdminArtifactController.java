package com.relic.controller.admin;

import com.relic.dto.ArtifactCreateDTO;
import com.relic.dto.ArtifactUpdateDTO;
import com.relic.result.Result;
import com.relic.service.ArtifactService;
import com.relic.vo.ArtifactDetailVO;
import com.relic.vo.ArtifactVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/artifact")
@RequiredArgsConstructor
@Tag(name = "管理端文物管理", description = "文物CRUD")
public class AdminArtifactController {

    private final ArtifactService artifactService;

    @GetMapping("/page")
    public Result<PageResultVO<ArtifactVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String titleZh,
            @RequestParam(required = false) String titleEn,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer dynastyId,
            @RequestParam(required = false) Integer museumId,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        return Result.success(artifactService.page(titleZh, titleEn, type, dynastyId,
                museumId, material, keyword, sortBy, sortOrder, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ArtifactDetailVO> getById(@PathVariable Integer id) {
        return Result.success(artifactService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody ArtifactCreateDTO dto) {
        artifactService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody ArtifactUpdateDTO dto) {
        artifactService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        artifactService.delete(id);
        return Result.success();
    }
}