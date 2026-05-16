package com.relic.service;

import com.relic.dto.AuditReviewDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface AuditRecordService {
    PageResultVO<Map<String, Object>> listAudits(String contentType, String manualAuditResult, int page, int pageSize);
    void audit(Long id, AuditReviewDTO dto);
}