package com.relic.controller.admin;

import com.relic.dto.ArtifactImageCreateDTO;
import com.relic.result.Result;
import com.relic.service.ArtifactImageService;
import com.relic.vo.ArtifactImageVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/artifact/{artifactId}/images")
@RequiredArgsConstructor
@Tag(name = "管理端文物图片管理", description = "文物多图片CRUD + 设置主图")
public class AdminArtifactImageController {

    private final ArtifactImageService artifactImageService;

    @GetMapping
    public Result<List<ArtifactImageVO>> list(@PathVariable Integer artifactId) {
        return Result.success(artifactImageService.getByArtifactId(artifactId));
    }

    @PostMapping
    public Result<Void> create(@PathVariable Integer artifactId, @RequestBody ArtifactImageCreateDTO dto) {
        artifactImageService.create(artifactId, dto);
        return Result.success();
    }

    @PutMapping("/{imageId}")
    public Result<Void> update(@PathVariable Integer artifactId, @PathVariable Integer imageId,
                               @RequestBody ArtifactImageCreateDTO dto) {
        artifactImageService.update(artifactId, imageId, dto);
        return Result.success();
    }

    @DeleteMapping("/{imageId}")
    public Result<Void> delete(@PathVariable Integer artifactId, @PathVariable Integer imageId) {
        artifactImageService.delete(artifactId, imageId);
        return Result.success();
    }

    @PutMapping("/{imageId}/primary")
    public Result<Void> setPrimary(@PathVariable Integer artifactId, @PathVariable Integer imageId) {
        artifactImageService.setPrimary(artifactId, imageId);
        return Result.success();
    }
}