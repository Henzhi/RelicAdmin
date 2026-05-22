package com.relic.service;

import com.relic.vo.PageResultVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface SecurityLogService {
    PageResultVO<Map<String, Object>> listPage(String eventType, Integer userId, String keyword,
                                                String startTime, String endTime,
                                                int page, int pageSize);

    void exportCSV(String eventType, Integer userId, String keyword,
                   String startTime, String endTime, HttpServletResponse response);

    void exportExcel(String eventType, Integer userId, String keyword,
                     String startTime, String endTime, HttpServletResponse response);

    void record(Long userId, String eventType, String ip, String detail);
}