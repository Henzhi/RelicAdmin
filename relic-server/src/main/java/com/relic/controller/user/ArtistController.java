package com.relic.controller.user;

import com.relic.result.Result;
import com.relic.service.ArtistService;
import com.relic.vo.ArtistVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/artist")
@RequiredArgsConstructor
@Tag(name = "用户端艺术家", description = "艺术家查询")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/list")
    public Result<PageResultVO<ArtistVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer dynastyId,
            @RequestParam(required = false) String keyword) {
        return Result.success(artistService.page(keyword, null, dynastyId, page, pageSize));
    }
}