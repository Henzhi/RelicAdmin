package com.relic.service;

import com.relic.dto.SensitiveWordCreateDTO;
import com.relic.dto.SensitiveWordUpdateDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface SensitiveWordService {
    PageResultVO<Map<String, Object>> page(String word, String category, Integer status, int page, int pageSize);
    void create(SensitiveWordCreateDTO dto);
    void update(Long id, SensitiveWordUpdateDTO dto);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
    void batchImport(String content);
}