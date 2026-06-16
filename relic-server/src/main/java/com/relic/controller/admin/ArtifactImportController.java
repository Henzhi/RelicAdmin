package com.relic.controller.admin;

import com.relic.entity.ImportHistory;
import com.relic.result.Result;
import com.relic.service.ArtifactImportService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin/artifact-import")
@RequiredArgsConstructor
@Slf4j
public class ArtifactImportController {

    private final ArtifactImportService artifactImportService;

    /**
     * 上传文件并预览数据
     */
    @PostMapping("/preview")
    public Result<Map<String, Object>> preview(@RequestParam("file") MultipartFile file) {
        log.info("文件预览: {}", file.getOriginalFilename());
        return Result.success(artifactImportService.preview(file));
    }

    /**
     * 确认导入
     */
    @PostMapping("/confirm")
    public Result<Map<String, Object>> confirmImport(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fieldMapping", required = false) String fieldMappingJson) {
        log.info("确认导入: {}", file.getOriginalFilename());
        Map<String, String> fieldMapping = null;
        if (fieldMappingJson != null && !fieldMappingJson.isBlank()) {
            fieldMapping = com.alibaba.fastjson2.JSON.parseObject(fieldMappingJson, Map.class);
        }
        return Result.success(artifactImportService.confirmImport(file, fieldMapping));
    }

    /**
     * 查询导入历史
     */
    @GetMapping("/history")
    public Result<PageResultVO<ImportHistory>> listHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(artifactImportService.listHistory(page, pageSize));
    }

    /**
     * 获取导入详情
     */
    @GetMapping("/history/{id}")
    public Result<ImportHistory> getHistoryDetail(@PathVariable Integer id) {
        return Result.success(artifactImportService.getHistoryDetail(id));
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate(@RequestParam(defaultValue = "xlsx") String fileType) {
        byte[] data = artifactImportService.downloadTemplate(fileType);
        String fileName = "文物数据导入模板." + fileType;
        String contentType = "csv".equals(fileType)
                ? "text/csv"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }
}
