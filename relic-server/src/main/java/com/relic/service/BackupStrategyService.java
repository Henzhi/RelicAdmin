package com.relic.service;

import com.relic.dto.BackupStrategyUpdateDTO;

import java.util.Map;

public interface BackupStrategyService {
    Map<String, Object> getStrategy();
    void updateStrategy(Integer id, BackupStrategyUpdateDTO dto);
}