package com.relic.service;

import com.relic.vo.PageResultVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface SystemLogService {
    PageResultVO<Map<String, Object>> listPage(String logLevel, String module, String keyword,
                                                String startTime, String endTime,
                                                int page, int pageSize);

    void exportCSV(String logLevel, String module, String keyword,
                   String startTime, String endTime, HttpServletResponse response);

    void exportExcel(String logLevel, String module, String keyword,
                     String startTime, String endTime, HttpServletResponse response);

    void record(String logLevel, String module, String message, String detail);
}