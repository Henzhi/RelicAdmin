package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.AuditBatchReviewDTO;
import com.relic.dto.AuditReviewDTO;
import com.relic.mapper.AuditRecordMapper;
import com.relic.mapper.UserCommentMapper;
import com.relic.mapper.UserPostMapper;
import com.relic.mapper.UserUploadMapper;
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
    private final UserPostMapper userPostMapper;
    private final UserCommentMapper userCommentMapper;
    private final UserUploadMapper userUploadMapper;

    @Override
    public PageResultVO<Map<String, Object>> listAudits(String contentType, String manualAuditResult,
                                                         String sourceType, String startDate, String endDate,
                                                         int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = auditRecordMapper.selectByPage(contentType, manualAuditResult, sourceType, startDate, endDate, offset, pageSize);
        long total = auditRecordMapper.countByPage(contentType, manualAuditResult, sourceType, startDate, endDate);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void audit(Long id, AuditReviewDTO dto) {
        Long auditorId = BaseContext.getCurrentId();
        if ("rejected".equals(dto.getManualAuditResult()) && (dto.getRejectReason() == null || dto.getRejectReason().isBlank())) {
            throw new IllegalArgumentException("拒绝时必须填写拒绝原因");
        }
        auditRecordMapper.updateAuditResult(id, dto.getManualAuditResult(), auditorId, dto.getRejectReason());
        // 同步更新源表审核状态
        syncSourceTableStatus(id, dto.getManualAuditResult(), auditorId, dto.getRejectReason());
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
        // 同步更新源表审核状态
        for (Long id : dto.getIds()) {
            syncSourceTableStatus(id, dto.getManualAuditResult(), auditorId, dto.getRejectReason());
        }
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

    @Override
    public List<Map<String, Object>> getContentTypeStats(String startDate, String endDate) {
        return auditRecordMapper.selectContentTypeStats(startDate, endDate);
    }

    /**
     * 审核操作后同步更新源表（user_posts/user_comments/user_uploads）的审核状态
     */
    private void syncSourceTableStatus(Long auditRecordId, String manualAuditResult, Long auditorId, String rejectReason) {
        try {
            Map<String, Object> record = auditRecordMapper.selectById(auditRecordId);
            if (record == null) return;

            String contentId = String.valueOf(record.get("contentId"));
            String sourceType = String.valueOf(record.get("sourceType"));
            Integer auditBy = auditorId != null ? auditorId.intValue() : null;

            // 从 content_id 中提取源表记录ID (格式: post_123, comment_456, upload_789)
            String[] parts = contentId.split("_");
            if (parts.length < 2) return;
            Integer sourceId;
            try {
                sourceId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return;
            }

            switch (sourceType) {
                case "user_posts" -> userPostMapper.updateAuditStatus(sourceId, manualAuditResult, auditBy, rejectReason);
                case "user_comments" -> userCommentMapper.updateAuditStatus(sourceId, manualAuditResult, auditBy, rejectReason);
                case "user_uploads" -> userUploadMapper.updateAuditStatus(sourceId, manualAuditResult, auditBy, rejectReason);
            }
            log.info("已同步源表 {} 记录 {} 状态为 {}", sourceType, sourceId, manualAuditResult);
        } catch (Exception e) {
            log.error("同步源表审核状态失败, auditRecordId={}", auditRecordId, e);
        }
    }
}