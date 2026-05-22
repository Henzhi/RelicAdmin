package com.relic.controller.admin;

import com.relic.annotation.OperationLog;
import com.relic.dto.AnnouncementCreateDTO;
import com.relic.result.Result;
import com.relic.service.AnnouncementService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/announcement")
@RequiredArgsConstructor
@Tag(name = "管理端-公告管理", description = "公告的创建、编辑、删除、上线/下线")
public class AnnouncementAdminController {

    private final AnnouncementService announcementService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(announcementService.listAdminAnnouncements(status, page, pageSize));
    }

    @PostMapping
    @OperationLog(operationType = "INSERT", targetType = "Announcement")
    public Result<Void> create(@RequestBody AnnouncementCreateDTO dto) {
        announcementService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperationLog(operationType = "UPDATE", targetType = "Announcement")
    public Result<Void> update(@PathVariable Long id, @RequestBody AnnouncementCreateDTO dto) {
        announcementService.update(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/offline")
    @OperationLog(operationType = "UPDATE", targetType = "Announcement")
    public Result<Void> offline(@PathVariable Long id) {
        announcementService.offline(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(operationType = "DELETE", targetType = "Announcement")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }
}