package com.relic.controller.admin;

import com.relic.dto.AppealReviewDTO;
import com.relic.result.Result;
import com.relic.service.AppealService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/appeal")
@RequiredArgsConstructor
@Tag(name = "管理端-申诉管理", description = "查看和处理用户申诉")
public class AppealAdminController {

    private final AppealService appealService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        return Result.success(appealService.listAdminAppeals(status, page, pageSize));
    }

    @PutMapping("/{id}")
    public Result<Void> handle(@PathVariable Long id, @RequestBody AppealReviewDTO dto) {
        appealService.handleAppeal(id, dto);
        return Result.success();
    }
}