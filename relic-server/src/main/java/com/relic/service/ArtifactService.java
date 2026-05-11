package com.relic.service;

import com.relic.dto.ArtifactCreateDTO;
import com.relic.dto.ArtifactUpdateDTO;
import com.relic.vo.ArtifactDetailVO;
import com.relic.vo.ArtifactVO;
import com.relic.vo.PageResultVO;

public interface ArtifactService {
    PageResultVO<ArtifactVO> page(String titleZh, String titleEn, String type, Integer dynastyId,
                                  Integer museumId, String material, String keyword,
                                  String sortBy, String sortOrder, int page, int pageSize);
    ArtifactDetailVO getById(Integer id);
    void create(ArtifactCreateDTO dto);
    void update(Integer id, ArtifactUpdateDTO dto);
    void delete(Integer id);
}