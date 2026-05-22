package com.relic.controller.admin;

import com.relic.annotation.OperationLog;
import com.relic.dto.DatasourceConfigCreateDTO;
import com.relic.dto.DatasourceConfigUpdateDTO;
import com.relic.dto.SystemConfigCreateDTO;
import com.relic.dto.SystemConfigUpdateDTO;
import com.relic.result.Result;
import com.relic.service.SystemConfigService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/system-config")
@RequiredArgsConstructor
@Tag(name = "管理端-系统配置管理", description = "全局参数与数据源连接管理")
public class SystemConfigAdminController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String configGroup) {
        return Result.success(systemConfigService.listPage(configGroup, page, pageSize));
    }

    @GetMapping("/group/{configGroup}")
    public Result<List<Map<String, Object>>> getByGroup(@PathVariable String configGroup) {
        return Result.success(systemConfigService.getByGroup(configGroup));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Integer id) {
        return Result.success(systemConfigService.getById(id));
    }

    @PostMapping
    @OperationLog(operationType = "INSERT", targetType = "SystemConfig")
    public Result<Void> create(@RequestBody SystemConfigCreateDTO dto) {
        systemConfigService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperationLog(operationType = "UPDATE", targetType = "SystemConfig")
    public Result<Void> update(@PathVariable Integer id, @RequestBody SystemConfigUpdateDTO dto) {
        systemConfigService.update(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/value")
    @OperationLog(operationType = "UPDATE", targetType = "SystemConfig")
    public Result<Void> updateValue(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        systemConfigService.updateValue(id, body.get("configValue"));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(operationType = "DELETE", targetType = "SystemConfig")
    public Result<Void> delete(@PathVariable Integer id) {
        systemConfigService.delete(id);
        return Result.success();
    }

    @GetMapping("/datasource/page")
    public Result<PageResultVO<Map<String, Object>>> datasourcePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String dsType,
            @RequestParam(required = false) String status) {
        return Result.success(systemConfigService.listDatasourcePage(dsType, status, page, pageSize));
    }

    @GetMapping("/datasource/{id}")
    public Result<Map<String, Object>> getDatasourceById(@PathVariable Integer id) {
        return Result.success(systemConfigService.getDatasourceById(id));
    }

    @PostMapping("/datasource")
    @OperationLog(operationType = "INSERT", targetType = "SystemConfig")
    public Result<Void> createDatasource(@RequestBody DatasourceConfigCreateDTO dto) {
        systemConfigService.createDatasource(dto);
        return Result.success();
    }

    @PutMapping("/datasource/{id}")
    @OperationLog(operationType = "UPDATE", targetType = "SystemConfig")
    public Result<Void> updateDatasource(@PathVariable Integer id, @RequestBody DatasourceConfigUpdateDTO dto) {
        systemConfigService.updateDatasource(id, dto);
        return Result.success();
    }

    @PostMapping("/datasource/{id}/test")
    @OperationLog(operationType = "UPDATE", targetType = "SystemConfig")
    public Result<String> testConnection(@PathVariable Integer id) {
        systemConfigService.testConnection(id);
        return Result.success("连接测试成功");
    }

    @DeleteMapping("/datasource/{id}")
    @OperationLog(operationType = "DELETE", targetType = "SystemConfig")
    public Result<Void> deleteDatasource(@PathVariable Integer id) {
        systemConfigService.deleteDatasource(id);
        return Result.success();
    }

    @GetMapping("/feature-toggles")
    public Result<List<Map<String, Object>>> getFeatureToggles() {
        return Result.success(systemConfigService.getByGroup("feature_toggle"));
    }

    @PutMapping("/feature-toggles/{id}/toggle")
    @OperationLog(operationType = "UPDATE", targetType = "SystemConfig")
    public Result<Void> toggleFeature(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        systemConfigService.updateValue(id, body.get("configValue"));
        return Result.success();
    }
}