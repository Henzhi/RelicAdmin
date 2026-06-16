package com.relic.service.impl;

import com.relic.dto.SystemConfigCreateDTO;
import com.relic.dto.SystemConfigUpdateDTO;
import com.relic.mapper.SystemConfigMapper;
import com.relic.service.SystemConfigService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    @Override
    public PageResultVO<Map<String, Object>> listPage(String configGroup, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = systemConfigMapper.selectByPage(configGroup, offset, pageSize);
        long total = systemConfigMapper.countByPage(configGroup);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public List<Map<String, Object>> getByGroup(String configGroup) {
        return systemConfigMapper.selectByGroup(configGroup);
    }

    @Override
    public Map<String, Object> getById(Integer id) {
        return systemConfigMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getByKey(String configKey) {
        return systemConfigMapper.selectByKey(configKey);
    }

    @Override
    @Transactional
    public void create(SystemConfigCreateDTO dto) {
        if (dto.getConfigKey() == null || dto.getConfigKey().isBlank()) {
            throw new IllegalArgumentException("配置键不能为空");
        }
        if (dto.getConfigName() == null || dto.getConfigName().isBlank()) {
            throw new IllegalArgumentException("配置名称不能为空");
        }
        if (systemConfigMapper.checkConfigKeyExists(dto.getConfigKey()) > 0) {
            throw new IllegalArgumentException("配置键已存在");
        }
        if (dto.getConfigType() == null) dto.setConfigType("string");
        if (dto.getConfigGroup() == null) dto.setConfigGroup("general");
        if (dto.getSortOrder() == null) dto.setSortOrder(0);

        systemConfigMapper.insert(dto.getConfigKey(), dto.getConfigName(), dto.getConfigValue(),
                dto.getConfigType(), dto.getConfigGroup(), dto.getDescription(), dto.getSortOrder());
        log.info("新增系统配置: {}", dto.getConfigKey());
    }

    @Override
    @Transactional
    public void update(Integer id, SystemConfigUpdateDTO dto) {
        if (dto.getConfigName() == null || dto.getConfigName().isBlank()) {
            throw new IllegalArgumentException("配置名称不能为空");
        }
        systemConfigMapper.update(id, dto.getConfigName(), dto.getConfigValue(),
                dto.getConfigType(), dto.getConfigGroup(), dto.getDescription(),
                dto.getSortOrder(), dto.getStatus());
        log.info("更新系统配置 id: {}", id);
    }

    @Override
    @Transactional
    public void updateValue(Integer id, String configValue) {
        systemConfigMapper.updateValue(id, configValue);
        log.info("更新系统配置值 id: {}", id);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        systemConfigMapper.deleteById(id);
        log.info("删除系统配置 id: {}", id);
    }
}
