package com.relic.service;

import com.relic.dto.PenaltyCreateDTO;
import com.relic.dto.PenaltyRevokeDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface PenaltyService {
    PageResultVO<Map<String, Object>> listViolationTypes();
    void createPenalty(PenaltyCreateDTO dto);
    PageResultVO<Map<String, Object>> listPenalties(Integer userId, String penaltyType, Integer status, int page, int pageSize);
    void revokePenalty(Long id, PenaltyRevokeDTO dto);
}