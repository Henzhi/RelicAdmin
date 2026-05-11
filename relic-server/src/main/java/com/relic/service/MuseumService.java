package com.relic.service;

import com.relic.dto.MuseumCreateDTO;
import com.relic.dto.MuseumUpdateDTO;
import com.relic.vo.MuseumVO;
import com.relic.vo.PageResultVO;

public interface MuseumService {
    PageResultVO<MuseumVO> page(String name, String country, int page, int pageSize);
    MuseumVO getById(Integer id);
    void create(MuseumCreateDTO dto);
    void update(Integer id, MuseumUpdateDTO dto);
    void delete(Integer id);
}