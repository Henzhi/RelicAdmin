package com.relic.service.impl;

import com.relic.dto.AuditStrategyUpdateDTO;
import com.relic.mapper.AuditStrategyMapper;
import com.relic.service.AuditStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditStrategyServiceImpl implements AuditStrategyService {

    private final AuditStrategyMapper auditStrategyMapper;

    @Override
    public List<Map<String, Object>> listAll() {
        return auditStrategyMapper.selectAll();
    }

    @Override
    public void initDefaults() {
        String[][] defaults = {
                {"post", "auto_review", "1", "0", "2"},
                {"comment", "auto_review", "1", "0", "2"},
                {"upload", "auto_review", "1", "1", "3"}
        };
        for (String[] def : defaults) {
            Map<String, Object> existing = auditStrategyMapper.selectByContentType(def[0]);
            if (existing == null || existing.isEmpty()) {
                auditStrategyMapper.insertDefault(def[0], def[1],
                        Integer.parseInt(def[2]), Integer.parseInt(def[3]), Integer.parseInt(def[4]));
                log.info("初始化审核策略: content_type={}", def[0]);
            }
        }
    }

    @Override
    public void updateStrategy(Integer id, AuditStrategyUpdateDTO dto) {
        auditStrategyMapper.update(id,
                dto.getAutoMode() != null ? dto.getAutoMode() : "auto_review",
                dto.getEnableSensitiveCheck() != null ? dto.getEnableSensitiveCheck() : 1,
                dto.getEnableImageCheck() != null ? dto.getEnableImageCheck() : 0,
                dto.getRiskThreshold() != null ? dto.getRiskThreshold() : 2,
                dto.getStatus() != null ? dto.getStatus() : 1);
        log.info("审核策略 id={} 已更新", id);
    }

    @Override
    public String getAutoMode(String contentType) {
        Map<String, Object> strategy = getStrategy(contentType);
        Object mode = strategy.get("autoMode");
        return mode != null ? mode.toString() : "auto_review";
    }

    @Override
    public Map<String, Object> getStrategy(String contentType) {
        Map<String, Object> strategy = auditStrategyMapper.selectByContentType(contentType);
        if (strategy == null || strategy.isEmpty()) {
            // 返回默认策略
            return Map.of(
                    "autoMode", "auto_review",
                    "enableSensitiveCheck", 1,
                    "enableImageCheck", 0,
                    "riskThreshold", 2
            );
        }
        return strategy;
    }
}