package com.relic.service.impl;

import com.relic.dto.DatasourceConfigCreateDTO;
import com.relic.dto.DatasourceConfigUpdateDTO;
import com.relic.dto.SystemConfigCreateDTO;
import com.relic.dto.SystemConfigUpdateDTO;
import com.relic.mapper.DatasourceConfigMapper;
import com.relic.mapper.SystemConfigMapper;
import com.relic.service.SystemConfigService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;
    private final DatasourceConfigMapper datasourceConfigMapper;

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

    @Override
    public PageResultVO<Map<String, Object>> listDatasourcePage(String dsType, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = datasourceConfigMapper.selectByPage(dsType, status, offset, pageSize);
        long total = datasourceConfigMapper.countByPage(dsType, status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public Map<String, Object> getDatasourceById(Integer id) {
        return datasourceConfigMapper.selectById(id);
    }

    @Override
    @Transactional
    public void createDatasource(DatasourceConfigCreateDTO dto) {
        if (dto.getDsName() == null || dto.getDsName().isBlank()) {
            throw new IllegalArgumentException("数据源名称不能为空");
        }
        if (dto.getDsKey() == null || dto.getDsKey().isBlank()) {
            throw new IllegalArgumentException("数据源标识不能为空");
        }
        if (dto.getHost() == null || dto.getHost().isBlank()) {
            throw new IllegalArgumentException("主机地址不能为空");
        }
        if (dto.getPort() == null) {
            throw new IllegalArgumentException("端口不能为空");
        }
        if (datasourceConfigMapper.checkDsKeyExists(dto.getDsKey()) > 0) {
            throw new IllegalArgumentException("数据源标识已存在");
        }
        if (dto.getMaxPoolSize() == null) dto.setMaxPoolSize(10);
        if (dto.getDsType() == null) dto.setDsType("mysql");

        datasourceConfigMapper.insert(dto.getDsName(), dto.getDsKey(), dto.getDsType(),
                dto.getHost(), dto.getPort(), dto.getDatabaseName(), dto.getUsername(),
                dto.getPasswordEncrypted(), dto.getExtraParams(), dto.getMaxPoolSize());
        log.info("新增数据源配置: {}", dto.getDsKey());
    }

    @Override
    @Transactional
    public void updateDatasource(Integer id, DatasourceConfigUpdateDTO dto) {
        if (dto.getDsName() == null || dto.getDsName().isBlank()) {
            throw new IllegalArgumentException("数据源名称不能为空");
        }
        datasourceConfigMapper.update(id, dto.getDsName(), dto.getDsType(), dto.getHost(),
                dto.getPort(), dto.getDatabaseName(), dto.getUsername(),
                dto.getPasswordEncrypted(), dto.getExtraParams(), dto.getMaxPoolSize());
        log.info("更新数据源配置 id: {}", id);
    }

    @Override
    @Transactional
    public void testConnection(Integer id) {
        Map<String, Object> ds = datasourceConfigMapper.selectById(id);
        if (ds == null) {
            throw new IllegalArgumentException("数据源配置不存在");
        }
        String host = (String) ds.get("host");
        Integer port = (Integer) ds.get("port");
        String dbName = (String) ds.get("databaseName");
        String username = (String) ds.get("username");
        String password = (String) ds.get("passwordEncrypted");
        String dsType = (String) ds.get("dsType");

        try {
            String jdbcUrl;
            if ("postgresql".equals(dsType)) {
                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
                Class.forName("org.postgresql.Driver");
            } else {
                jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                        + "?connectTimeout=5000&socketTimeout=5000";
            }
            try (java.sql.Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
                datasourceConfigMapper.updateStatus(id, "connected", "连接成功 (MySQL " +
                        conn.getMetaData().getDatabaseProductVersion() + ")");
            }
            log.info("数据源连接测试成功: dsKey={}", ds.get("dsKey"));
        } catch (Exception e) {
            datasourceConfigMapper.updateStatus(id, "failed", "连接失败: " + e.getMessage());
            log.warn("数据源连接测试失败: dsKey={}, error={}", ds.get("dsKey"), e.getMessage());
            throw new RuntimeException("连接测试失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteDatasource(Integer id) {
        datasourceConfigMapper.deleteById(id);
        log.info("删除数据源配置 id: {}", id);
    }
}