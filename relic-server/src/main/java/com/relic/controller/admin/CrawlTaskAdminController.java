package com.relic.controller.admin;

import com.relic.dto.CrawlTaskCreateDTO;
import com.relic.dto.CrawlTaskUpdateDTO;
import com.relic.result.Result;
import com.relic.service.CrawlTaskService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/crawl-task")
@RequiredArgsConstructor
@Tag(name = "管理端-爬取任务管理", description = "爬取任务CRUD与执行控制")
public class CrawlTaskAdminController {

    private final CrawlTaskService crawlTaskService;

    @GetMapping("/page")
    public Result<PageResultVO<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer priority) {
        return Result.success(crawlTaskService.listPage(status, priority, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Integer id) {
        return Result.success(crawlTaskService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody CrawlTaskCreateDTO dto,
                               @RequestAttribute(value = "userId", required = false) Integer userId) {
        crawlTaskService.create(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody CrawlTaskUpdateDTO dto) {
        crawlTaskService.update(id, dto);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result<String> execute(@PathVariable Integer id,
                                  @RequestAttribute(value = "userId", required = false) Integer userId) {
        crawlTaskService.execute(id, userId);
        return Result.success("任务执行成功");
    }

    @PostMapping("/{id}/pause")
    public Result<Void> pause(@PathVariable Integer id) {
        crawlTaskService.pause(id);
        return Result.success();
    }

    @PostMapping("/{id}/resume")
    public Result<Void> resume(@PathVariable Integer id) {
        crawlTaskService.resume(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        crawlTaskService.delete(id);
        return Result.success();
    }

    @GetMapping("/logs/page")
    public Result<PageResultVO<Map<String, Object>>> logsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false) String status) {
        return Result.success(crawlTaskService.listLogs(taskId, status, page, pageSize));
    }
}