package com.relic.controller.admin;

import com.relic.dto.SensitiveWordCreateDTO;
import com.relic.dto.SensitiveWordUpdateDTO;
import com.relic.result.Result;
import com.relic.service.SensitiveWordService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/sensitive-word")
@RequiredArgsConstructor
@Tag(name = "管理端-敏感词库", description = "敏感词的增删改查、批量导入、启用/禁用")
public class SensitiveWordAdminController {

    private final SensitiveWordService sensitiveWordService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String word,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.success(sensitiveWordService.page(word, category, status, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestBody SensitiveWordCreateDTO dto) {
        sensitiveWordService.create(dto);
        return Result.success();
    }

    @PostMapping("/import")
    public Result<Void> batchImport(@RequestBody Map<String, String> body) {
        sensitiveWordService.batchImport(body.get("content"));
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SensitiveWordUpdateDTO dto) {
        sensitiveWordService.update(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        sensitiveWordService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sensitiveWordService.delete(id);
        return Result.success();
    }
}