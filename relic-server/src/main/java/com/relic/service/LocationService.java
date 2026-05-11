package com.relic.service;

import com.relic.dto.LocationCreateDTO;
import com.relic.dto.LocationUpdateDTO;
import com.relic.vo.LocationVO;

import java.util.List;

public interface LocationService {
    List<LocationVO> tree();
    List<LocationVO> list(String type, Integer parentId);
    void create(LocationCreateDTO dto);
    void update(Integer id, LocationUpdateDTO dto);
    void delete(Integer id);
}