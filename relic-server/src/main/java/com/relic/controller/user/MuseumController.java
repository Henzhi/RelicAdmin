package com.relic.controller.user;

import com.relic.result.Result;
import com.relic.service.MuseumService;
import com.relic.vo.MuseumVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/museum")
@RequiredArgsConstructor
@Tag(name = "用户端博物馆", description = "博物馆查询")
public class MuseumController {

    private final MuseumService museumService;

    @GetMapping("/list")
    public Result<PageResultVO<MuseumVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String keyword) {
        return Result.success(museumService.page(keyword, country, page, pageSize));
    }
}