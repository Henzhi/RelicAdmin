package com.relic.controller.admin;

import com.relic.entity.UserFavorite;
import com.relic.mapper.UserFavoriteMapper;
import com.relic.result.Result;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/favorite")
@RequiredArgsConstructor
@Tag(name = "管理端-用户收藏管理", description = "用户收藏记录列表、删除")
public class AdminFavoriteController {

    private final UserFavoriteMapper userFavoriteMapper;

    @GetMapping("/page")
    public Result<PageResultVO<UserFavorite>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId) {
        int offset = (page - 1) * pageSize;
        List<UserFavorite> records = userFavoriteMapper.selectAllByPage(userId, offset, pageSize);
        long total = userFavoriteMapper.countAllByPage(userId);
        return Result.success(new PageResultVO<>(total, records, page, pageSize));
    }

    @DeleteMapping("/{userId}/{artifactId}")
    public Result<Void> delete(@PathVariable Integer userId, @PathVariable Integer artifactId) {
        userFavoriteMapper.delete(userId, artifactId);
        return Result.success();
    }
}