package com.relic.service;

import com.relic.dto.DatasourceConfigCreateDTO;
import com.relic.dto.DatasourceConfigUpdateDTO;
import com.relic.dto.SystemConfigCreateDTO;
import com.relic.dto.SystemConfigUpdateDTO;
import com.relic.vo.PageResultVO;

import java.util.List;
import java.util.Map;

public interface SystemConfigService {
    PageResultVO<Map<String, Object>> listPage(String configGroup, int page, int pageSize);

    List<Map<String, Object>> getByGroup(String configGroup);

    Map<String, Object> getById(Integer id);

    Map<String, Object> getByKey(String configKey);

    void create(SystemConfigCreateDTO dto);

    void update(Integer id, SystemConfigUpdateDTO dto);

    void updateValue(Integer id, String configValue);

    void delete(Integer id);

    PageResultVO<Map<String, Object>> listDatasourcePage(String dsType, String status, int page, int pageSize);

    Map<String, Object> getDatasourceById(Integer id);

    void createDatasource(DatasourceConfigCreateDTO dto);

    void updateDatasource(Integer id, DatasourceConfigUpdateDTO dto);

    void testConnection(Integer id);

    void deleteDatasource(Integer id);
}