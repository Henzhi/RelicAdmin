package com.relic.service;

import com.relic.dto.ArtistCreateDTO;
import com.relic.dto.ArtistUpdateDTO;
import com.relic.vo.ArtistVO;
import com.relic.vo.PageResultVO;

public interface ArtistService {
    PageResultVO<ArtistVO> page(String nameZh, String nameEn, Integer dynastyId, int page, int pageSize);
    ArtistVO getById(Integer id);
    void create(ArtistCreateDTO dto);
    void update(Integer id, ArtistUpdateDTO dto);
    void delete(Integer id);
}