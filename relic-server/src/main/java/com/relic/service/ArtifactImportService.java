package com.relic.service;

import com.relic.entity.ImportHistory;
import com.relic.vo.PageResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ArtifactImportService {

    /**
     * 上传并解析文件，返回预览数据（不实际导入）
     */
    Map<String, Object> preview(MultipartFile file);

    /**
     * 确认导入，将预览数据写入数据库
     */
    Map<String, Object> confirmImport(MultipartFile file, Map<String, String> fieldMapping);

    /**
     * 查询导入历史
     */
    PageResultVO<ImportHistory> listHistory(int page, int pageSize);

    /**
     * 获取导入详情（含错误报告）
     */
    ImportHistory getHistoryDetail(Integer id);

    /**
     * 下载导入模板
     */
    byte[] downloadTemplate(String fileType);
}
