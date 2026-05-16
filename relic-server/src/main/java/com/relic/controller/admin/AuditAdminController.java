package com.relic.controller.admin;

import com.relic.dto.AuditReviewDTO;
import com.relic.result.Result;
import com.relic.service.AuditRecordService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@Tag(name = "管理端-审核管理", description = "审核记录的查看与审核操作")
public class AuditAdminController {

    private final AuditRecordService auditRecordService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) String manualAuditResult) {
        return Result.success(auditRecordService.listAudits(contentType, manualAuditResult, page, pageSize));
    }

    @PutMapping("/{id}")
    public Result<Void> audit(@PathVariable Long id, @RequestBody AuditReviewDTO dto) {
        auditRecordService.audit(id, dto);
        return Result.success();
    }
}