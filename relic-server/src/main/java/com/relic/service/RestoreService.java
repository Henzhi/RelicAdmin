package com.relic.service;

import com.relic.dto.RestoreConfirmDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface RestoreService {
    PageResultVO<Map<String, Object>> page(Integer status, int page, int pageSize);
    Map<String, Object> restore(Long backupId, RestoreConfirmDTO dto);
    Map<String, Object> getRestoreDetail(Long id);
}