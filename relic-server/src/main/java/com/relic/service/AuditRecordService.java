package com.relic.service;

import com.relic.dto.AuditBatchReviewDTO;
import com.relic.dto.AuditReviewDTO;
import com.relic.vo.PageResultVO;

import java.util.List;
import java.util.Map;

public interface AuditRecordService {
    PageResultVO<Map<String, Object>> listAudits(String contentType, String manualAuditResult,
                                                  String sourceType, String startDate, String endDate,
                                                  int page, int pageSize);
    void audit(Long id, AuditReviewDTO dto);
    void batchAudit(AuditBatchReviewDTO dto);
    Map<String, Object> getStats(String startDate, String endDate);
    List<Map<String, Object>> getAuditorStats(String startDate, String endDate);
    List<Map<String, Object>> getContentTypeStats(String startDate, String endDate);
}