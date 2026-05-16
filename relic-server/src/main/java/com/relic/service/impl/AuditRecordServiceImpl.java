package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.AuditBatchReviewDTO;
import com.relic.dto.AuditReviewDTO;
import com.relic.mapper.AuditRecordMapper;
import com.relic.service.AuditRecordService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditRecordServiceImpl implements AuditRecordService {

    private final AuditRecordMapper auditRecordMapper;

    @Override
    public PageResultVO<Map<String, Object>> listAudits(String contentType, String manualAuditResult,
                                                         String sourceType, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = auditRecordMapper.selectByPage(contentType, manualAuditResult, sourceType, offset, pageSize);
        long total = auditRecordMapper.countByPage(contentType, manualAuditResult, sourceType);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void audit(Long id, AuditReviewDTO dto) {
        Long auditorId = BaseContext.getCurrentId();
        if ("rejected".equals(dto.getManualAuditResult()) && (dto.getRejectReason() == null || dto.getRejectReason().isBlank())) {
            throw new IllegalArgumentException("拒绝时必须填写拒绝原因");
        }
        auditRecordMapper.updateAuditResult(id, dto.getManualAuditResult(), auditorId, dto.getRejectReason());
        log.info("审核记录 {} 被管理员 {} 审核为 {}", id, auditorId, dto.getManualAuditResult());
    }

    @Override
    public void batchAudit(AuditBatchReviewDTO dto) {
        Long auditorId = BaseContext.getCurrentId();
        if (dto.getIds() == null || dto.getIds().length == 0) {
            throw new IllegalArgumentException("请选择待审核的记录");
        }
        if ("rejected".equals(dto.getManualAuditResult()) && (dto.getRejectReason() == null || dto.getRejectReason().isBlank())) {
            throw new IllegalArgumentException("批量拒绝时必须填写拒绝原因");
        }
        auditRecordMapper.batchUpdateResult(dto.getIds(), dto.getManualAuditResult(), auditorId, dto.getRejectReason());
        log.info("管理员 {} 批量审核 {} 条记录为 {}", auditorId, dto.getIds().length, dto.getManualAuditResult());
    }

    @Override
    public Map<String, Object> getStats(String startDate, String endDate) {
        return auditRecordMapper.selectStats(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getAuditorStats(String startDate, String endDate) {
        return auditRecordMapper.selectAuditorStats(startDate, endDate);
    }
}