package com.relic.service;

import com.relic.dto.ArtifactTypeCreateDTO;
import com.relic.vo.ArtifactTypeVO;
import com.relic.vo.PageResultVO;

import java.util.List;

public interface ArtifactTypeService {
    PageResultVO<ArtifactTypeVO> page(String name, int page, int pageSize);
    List<ArtifactTypeVO> listAll();
    void create(ArtifactTypeCreateDTO dto);
    void update(Integer id, ArtifactTypeCreateDTO dto);
    void delete(Integer id);
}