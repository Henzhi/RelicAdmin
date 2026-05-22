package com.relic.controller.admin;

import com.relic.result.Result;
import com.relic.service.OperationLogService;
import com.relic.service.SecurityLogService;
import com.relic.service.SystemLogService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理端-日志管理", description = "操作日志、系统日志、安全日志查询与导出")
public class LogAdminController {

    private final OperationLogService operationLogService;
    private final SystemLogService systemLogService;
    private final SecurityLogService securityLogService;

    @GetMapping("/operation-log/page")
    public Result<PageResultVO<Map<String, Object>>> operationLogPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(operationLogService.listPage(
                userId, operationType, targetType, keyword, startTime, endTime, page, pageSize));
    }

    @GetMapping("/operation-log/export-csv")
    public void exportOperationLogCSV(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        operationLogService.exportCSV(userId, operationType, targetType, keyword, startTime, endTime, response);
    }

    @GetMapping("/operation-log/export-excel")
    public void exportOperationLogExcel(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        operationLogService.exportExcel(userId, operationType, targetType, keyword, startTime, endTime, response);
    }

    @GetMapping("/system-log/page")
    public Result<PageResultVO<Map<String, Object>>> systemLogPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(systemLogService.listPage(
                logLevel, module, keyword, startTime, endTime, page, pageSize));
    }

    @GetMapping("/system-log/export-csv")
    public void exportSystemLogCSV(
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        systemLogService.exportCSV(logLevel, module, keyword, startTime, endTime, response);
    }

    @GetMapping("/system-log/export-excel")
    public void exportSystemLogExcel(
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        systemLogService.exportExcel(logLevel, module, keyword, startTime, endTime, response);
    }

    @GetMapping("/security-log/page")
    public Result<PageResultVO<Map<String, Object>>> securityLogPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(securityLogService.listPage(
                eventType, userId, keyword, startTime, endTime, page, pageSize));
    }

    @GetMapping("/security-log/export-csv")
    public void exportSecurityLogCSV(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        securityLogService.exportCSV(eventType, userId, keyword, startTime, endTime, response);
    }

    @GetMapping("/security-log/export-excel")
    public void exportSecurityLogExcel(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        securityLogService.exportExcel(eventType, userId, keyword, startTime, endTime, response);
    }
}