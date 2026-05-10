package com.relic.service;

import com.relic.dto.*;
import com.relic.vo.PageResultVO;
import com.relic.vo.PermissionVO;

import java.util.List;

public interface PermissionService {
    PageResultVO<PermissionVO> page(String name, String module, int page, int pageSize);
    List<PermissionVO> listAll();
    void create(PermissionCreateDTO dto);
    void update(Integer id, PermissionUpdateDTO dto);
    void delete(Integer id);
}