package com.relic.controller.user;

import com.relic.context.BaseContext;
import com.relic.dto.FavoriteCreateDTO;
import com.relic.result.Result;
import com.relic.service.FavoriteService;
import com.relic.vo.FavoriteVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/favorite")
@RequiredArgsConstructor
@Tag(name = "用户收藏模块", description = "收藏文物、查询收藏列表")
public class UserFavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public Result<Void> add(@RequestBody FavoriteCreateDTO dto) {
        Long userId = BaseContext.getCurrentId();
        favoriteService.add(userId.intValue(), dto);
        return Result.success();
    }

    @DeleteMapping("/{artifactId}")
    public Result<Void> remove(@PathVariable Integer artifactId) {
        Long userId = BaseContext.getCurrentId();
        favoriteService.remove(userId.intValue(), artifactId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResultVO<FavoriteVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String groupName) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(favoriteService.list(userId.intValue(), groupName, page, pageSize));
    }

    @GetMapping("/check/{artifactId}")
    public Result<Map<String, Boolean>> check(@PathVariable Integer artifactId) {
        Long userId = BaseContext.getCurrentId();
        boolean favorited = favoriteService.isFavorited(userId.intValue(), artifactId);
        return Result.success(Map.of("favorited", favorited));
    }
}