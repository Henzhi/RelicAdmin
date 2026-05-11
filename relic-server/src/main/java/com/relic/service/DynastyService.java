package com.relic.service;

import com.relic.dto.DynastyCreateDTO;
import com.relic.dto.DynastyUpdateDTO;
import com.relic.vo.DynastyVO;
import com.relic.vo.PageResultVO;

import java.util.List;

public interface DynastyService {
    PageResultVO<DynastyVO> page(String nameZh, int page, int pageSize);
    List<DynastyVO> listAll();
    void create(DynastyCreateDTO dto);
    void update(Integer id, DynastyUpdateDTO dto);
    void delete(Integer id);
}