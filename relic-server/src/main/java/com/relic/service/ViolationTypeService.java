package com.relic.service;

import com.relic.dto.ViolationTypeCreateDTO;
import com.relic.dto.ViolationTypeUpdateDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface ViolationTypeService {
    PageResultVO<Map<String, Object>> listPage(Integer status, int page, int pageSize);
    void create(ViolationTypeCreateDTO dto);
    void update(Integer id, ViolationTypeUpdateDTO dto);
    void updateStatus(Integer id, Integer status);
    void delete(Integer id);
}