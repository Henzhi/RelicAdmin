package com.relic.controller.admin;

import com.relic.annotation.OperationLog;
import com.relic.dto.AuditStrategyUpdateDTO;
import com.relic.result.Result;
import com.relic.service.AuditStrategyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/audit-strategy")
@RequiredArgsConstructor
@Tag(name = "管理端-审核策略", description = "配置各内容类型的自动审核策略")
public class AuditStrategyAdminController {

    private final AuditStrategyService auditStrategyService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.success(auditStrategyService.listAll());
    }

    @PutMapping("/{id}")
    @OperationLog(operationType = "UPDATE", targetType = "AuditStrategy")
    public Result<Void> update(@PathVariable Integer id, @RequestBody AuditStrategyUpdateDTO dto) {
        auditStrategyService.updateStrategy(id, dto);
        return Result.success();
    }
}