package com.relic.controller.user;

import com.relic.dto.AppealCreateDTO;
import com.relic.result.Result;
import com.relic.service.AppealService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/appeal")
@RequiredArgsConstructor
@Tag(name = "用户端-申诉", description = "用户提交申诉和查看自己的申诉记录")
public class AppealUserController {

    private final AppealService appealService;

    @PostMapping
    public Result<Void> create(@RequestBody AppealCreateDTO dto) {
        appealService.createAppeal(dto);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResultVO<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(appealService.listUserAppeals(page, pageSize));
    }
}