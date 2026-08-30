package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.AppealCreateDTO;
import com.relic.dto.AppealReviewDTO;
import com.relic.mapper.AppealRecordMapper;
import com.relic.mapper.PenaltyRecordMapper;
import com.relic.service.AppealService;
import com.relic.vo.PageQuery;
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
    private final PenaltyRecordMapper penaltyRecordMapper;

    @Override
    public void createAppeal(AppealCreateDTO dto) {
        Long currentUserId = BaseContext.getCurrentId();
        if (dto.getPenaltyId() == null) {
            throw new IllegalArgumentException("关联处罚记录ID不能为空");
        }
        if (dto.getAppealReason() == null || dto.getAppealReason().isBlank()) {
            throw new IllegalArgumentException("申诉理由不能为空");
        }
        // 只能针对自己的处罚记录申诉，防止越权提交
        Map<String, Object> penalty = penaltyRecordMapper.selectById(dto.getPenaltyId());
        if (penalty == null) {
            throw new IllegalArgumentException("处罚记录不存在");
        }
        if (!String.valueOf(currentUserId).equals(String.valueOf(penalty.get("userId")))) {
            throw new IllegalArgumentException("只能对本人收到的处罚提交申诉");
        }
        appealRecordMapper.insert(dto.getPenaltyId(), currentUserId.intValue(), dto.getAppealReason(), dto.getEvidence());
        log.info("用户 {} 提交了申诉，关联处罚记录 {}", currentUserId, dto.getPenaltyId());
    }

    @Override
    public PageResultVO<Map<String, Object>> listUserAppeals(int page, int pageSize) {
        Long currentUserId = BaseContext.getCurrentId();
        PageQuery pq = PageQuery.of(page, pageSize);
        List<Map<String, Object>> records = appealRecordMapper.selectByUserId(currentUserId.intValue(), pq.getOffset(), pq.getPageSize());
        long total = appealRecordMapper.countByUserId(currentUserId.intValue());
        return pq.toResult(total, records);
    }

    @Override
    public PageResultVO<Map<String, Object>> listAdminAppeals(String status, int page, int pageSize) {
        PageQuery pq = PageQuery.of(page, pageSize);
        List<Map<String, Object>> records = appealRecordMapper.selectAdminPage(status, pq.getOffset(), pq.getPageSize());
        long total = appealRecordMapper.countAdminPage(status);
        return pq.toResult(total, records);
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