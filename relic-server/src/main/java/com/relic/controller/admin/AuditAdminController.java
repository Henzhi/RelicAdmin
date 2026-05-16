package com.relic.controller.admin;

import com.relic.dto.AuditBatchReviewDTO;
import com.relic.dto.AuditReviewDTO;
import com.relic.result.Result;
import com.relic.service.AuditRecordService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@Tag(name = "管理端-审核管理", description = "审核记录的查看、单条/批量审核、审核统计")
public class AuditAdminController {

    private final AuditRecordService auditRecordService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) String manualAuditResult,
            @RequestParam(required = false) String sourceType) {
        return Result.success(auditRecordService.listAudits(contentType, manualAuditResult, sourceType, page, pageSize));
    }

    @PutMapping("/{id}")
    public Result<Void> audit(@PathVariable Long id, @RequestBody AuditReviewDTO dto) {
        auditRecordService.audit(id, dto);
        return Result.success();
    }

    @PutMapping("/batch")
    public Result<Void> batchAudit(@RequestBody AuditBatchReviewDTO dto) {
        auditRecordService.batchAudit(dto);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(auditRecordService.getStats(startDate, endDate));
    }

    @GetMapping("/auditor-stats")
    public Result<List<Map<String, Object>>> auditorStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(auditRecordService.getAuditorStats(startDate, endDate));
    }
}