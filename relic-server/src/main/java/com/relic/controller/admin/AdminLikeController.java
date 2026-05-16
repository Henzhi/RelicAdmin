package com.relic.controller.admin;

import com.relic.entity.ArtifactLike;
import com.relic.mapper.ArtifactLikeMapper;
import com.relic.result.Result;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/like")
@RequiredArgsConstructor
@Tag(name = "管理端-用户点赞管理", description = "用户点赞记录列表、删除")
public class AdminLikeController {

    private final ArtifactLikeMapper artifactLikeMapper;

    @GetMapping("/page")
    public Result<PageResultVO<ArtifactLike>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ArtifactLike> records = artifactLikeMapper.selectByPage(offset, pageSize);
        long total = artifactLikeMapper.countAll();
        return Result.success(new PageResultVO<>(total, records, page, pageSize));
    }

    @DeleteMapping("/{userId}/{artifactId}")
    public Result<Void> delete(@PathVariable Integer userId, @PathVariable Integer artifactId) {
        artifactLikeMapper.delete(userId, artifactId);
        return Result.success();
    }
}