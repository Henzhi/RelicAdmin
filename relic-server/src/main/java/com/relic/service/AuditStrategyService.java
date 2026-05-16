package com.relic.service;

import com.relic.dto.AuditStrategyUpdateDTO;

import java.util.List;
import java.util.Map;

public interface AuditStrategyService {
    List<Map<String, Object>> listAll();
    void initDefaults();
    void updateStrategy(Integer id, AuditStrategyUpdateDTO dto);
    String getAutoMode(String contentType);
}