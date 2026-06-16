package com.relic.service;

import com.relic.entity.Dynasty;
import com.relic.vo.PageResultVO;

public interface DynastyService {
    PageResultVO<Dynasty> page(String nameZh, int page, int pageSize);
    Dynasty getById(Integer id);
    void create(Dynasty dynasty);
    void update(Dynasty dynasty);
    void delete(Integer id);
}
