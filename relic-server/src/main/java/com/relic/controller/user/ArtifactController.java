package com.relic.controller.user;

import com.relic.result.Result;
import com.relic.service.ArtifactService;
import com.relic.vo.ArtifactDetailVO;
import com.relic.vo.ArtifactVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/artifact")
@RequiredArgsConstructor
@Tag(name = "用户端文物", description = "文物查询")
public class ArtifactController {

    private final ArtifactService artifactService;

    @GetMapping("/list")
    public Result<PageResultVO<ArtifactVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer dynastyId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer museumId,
            @RequestParam(required = false) String material,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        return Result.success(artifactService.page(null, null, type, dynastyId,
                museumId, material, keyword, sortBy, sortOrder, page, pageSize));
    }

    @GetMapping("/detail/{id}")
    public Result<ArtifactDetailVO> detail(@PathVariable Integer id) {
        return Result.success(artifactService.getById(id));
    }
}