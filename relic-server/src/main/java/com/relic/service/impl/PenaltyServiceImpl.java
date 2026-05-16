package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.PenaltyCreateDTO;
import com.relic.dto.PenaltyRevokeDTO;
import com.relic.mapper.PenaltyRecordMapper;
import com.relic.mapper.ViolationTypeMapper;
import com.relic.service.PenaltyService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRecordMapper penaltyRecordMapper;
    private final ViolationTypeMapper violationTypeMapper;

    @Override
    public PageResultVO<Map<String, Object>> listViolationTypes() {
        List<Map<String, Object>> records = violationTypeMapper.selectAll();
        return new PageResultVO<>((long) records.size(), records, 1, records.size());
    }

    @Override
    public void createPenalty(PenaltyCreateDTO dto) {
        Long operatorId = BaseContext.getCurrentId();
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("被处罚用户ID不能为空");
        }
        if (dto.getPenaltyType() == null || dto.getPenaltyType().isBlank()) {
            throw new IllegalArgumentException("处罚类型不能为空");
        }
        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new IllegalArgumentException("处罚原因不能为空");
        }
        if ("temp_ban".equals(dto.getPenaltyType()) && (dto.getExpireTime() == null || dto.getExpireTime().isBlank())) {
            throw new IllegalArgumentException("临时封禁必须指定过期时间");
        }
        penaltyRecordMapper.insert(dto.getUserId(), dto.getPenaltyType(), dto.getReason(),
                operatorId, dto.getExpireTime(), dto.getRemark());
        log.info("管理员 {} 对用户 {} 执行处罚: {}", operatorId, dto.getUserId(), dto.getPenaltyType());
    }

    @Override
    public PageResultVO<Map<String, Object>> listPenalties(Integer userId, String penaltyType, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = penaltyRecordMapper.selectByPage(userId, penaltyType, status, offset, pageSize);
        long total = penaltyRecordMapper.countByPage(userId, penaltyType, status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void revokePenalty(Long id, PenaltyRevokeDTO dto) {
        penaltyRecordMapper.updateRevoke(id, dto.getRemark());
        log.info("处罚记录 {} 已被解除", id);
    }
}