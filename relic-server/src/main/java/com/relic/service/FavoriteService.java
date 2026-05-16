package com.relic.service;

import com.relic.dto.FavoriteCreateDTO;
import com.relic.vo.FavoriteVO;
import com.relic.vo.PageResultVO;

public interface FavoriteService {
    void add(Integer userId, FavoriteCreateDTO dto);
    void remove(Integer userId, Integer artifactId);
    PageResultVO<FavoriteVO> list(Integer userId, String groupName, Integer page, Integer pageSize);
    boolean isFavorited(Integer userId, Integer artifactId);
}