package com.relic.service;

import com.relic.vo.PageResultVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface OperationLogService {
    PageResultVO<Map<String, Object>> listPage(Integer userId, String operationType, String targetType,
                                                String keyword, String startTime, String endTime,
                                                int page, int pageSize);

    void exportCSV(Integer userId, String operationType, String targetType,
                   String keyword, String startTime, String endTime,
                   HttpServletResponse response);

    void exportExcel(Integer userId, String operationType, String targetType,
                     String keyword, String startTime, String endTime,
                     HttpServletResponse response);

    void record(Long userId, String operationType, String targetType, String targetId,
                String oldValue, String newValue, String ip, String userAgent);
}