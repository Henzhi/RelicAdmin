package com.relic.service;

import com.relic.dto.CrawlTaskCreateDTO;
import com.relic.dto.CrawlTaskUpdateDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface CrawlTaskService {
    PageResultVO<Map<String, Object>> listPage(String status, Integer priority, int page, int pageSize);

    Map<String, Object> getById(Integer id);

    void create(CrawlTaskCreateDTO dto, Integer createdBy);

    void update(Integer id, CrawlTaskUpdateDTO dto);

    void execute(Integer id, Integer executorId);

    void pause(Integer id);

    void resume(Integer id);

    void delete(Integer id);

    PageResultVO<Map<String, Object>> listLogs(Integer taskId, String status, int page, int pageSize);
}