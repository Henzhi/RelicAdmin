package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.AppealCreateDTO;
import com.relic.dto.AppealReviewDTO;
import com.relic.mapper.AppealRecordMapper;
import com.relic.service.AppealService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppealServiceImpl implements AppealService {

    private final AppealRecordMapper appealRecordMapper;

    @Override
    public void createAppeal(AppealCreateDTO dto) {
        Long currentUserId = BaseContext.getCurrentId();
        if (dto.getPenaltyId() == null) {
            throw new IllegalArgumentException("关联处罚记录ID不能为空");
        }
        if (dto.getAppealReason() == null || dto.getAppealReason().isBlank()) {
            throw new IllegalArgumentException("申诉理由不能为空");
        }
        appealRecordMapper.insert(dto.getPenaltyId(), currentUserId.intValue(), dto.getAppealReason(), dto.getEvidence());
        log.info("用户 {} 提交了申诉，关联处罚记录 {}", currentUserId, dto.getPenaltyId());
    }

    @Override
    public PageResultVO<Map<String, Object>> listUserAppeals(int page, int pageSize) {
        Long currentUserId = BaseContext.getCurrentId();
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = appealRecordMapper.selectByUserId(currentUserId.intValue(), offset, pageSize);
        long total = appealRecordMapper.countByUserId(currentUserId.intValue());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listAdminAppeals(String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = appealRecordMapper.selectAdminPage(status, offset, pageSize);
        long total = appealRecordMapper.countAdminPage(status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void handleAppeal(Long id, AppealReviewDTO dto) {
        Long reviewerId = BaseContext.getCurrentId();
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new IllegalArgumentException("处理结果为必填项");
        }
        appealRecordMapper.updateReview(id, dto.getStatus(), dto.getReviewResult(), reviewerId, dto.getReviewRemark());
        log.info("管理员 {} 处理申诉 {}，结果：{}", reviewerId, id, dto.getStatus());
    }
}