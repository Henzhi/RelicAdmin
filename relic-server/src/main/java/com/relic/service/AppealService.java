package com.relic.service;

import com.relic.dto.AppealCreateDTO;
import com.relic.dto.AppealReviewDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface AppealService {
    void createAppeal(AppealCreateDTO dto);
    PageResultVO<Map<String, Object>> listUserAppeals(int page, int pageSize);
    PageResultVO<Map<String, Object>> listAdminAppeals(String status, int page, int pageSize);
    void handleAppeal(Long id, AppealReviewDTO dto);
}