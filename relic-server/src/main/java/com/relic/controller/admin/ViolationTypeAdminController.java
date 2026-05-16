package com.relic.controller.admin;

import com.relic.dto.ViolationTypeCreateDTO;
import com.relic.dto.ViolationTypeUpdateDTO;
import com.relic.result.Result;
import com.relic.service.ViolationTypeService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/violation-type")
@RequiredArgsConstructor
@Tag(name = "管理端-违规类型管理", description = "违规类型CRUD")
public class ViolationTypeAdminController {

    private final ViolationTypeService violationTypeService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(violationTypeService.listPage(status, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestBody ViolationTypeCreateDTO dto) {
        violationTypeService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody ViolationTypeUpdateDTO dto) {
        violationTypeService.update(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        violationTypeService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        violationTypeService.delete(id);
        return Result.success();
    }
}