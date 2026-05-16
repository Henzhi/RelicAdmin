package com.relic.service;

import com.relic.entity.UserPost;
import com.relic.vo.PageResultVO;

public interface PostService {
    PageResultVO<UserPost> page(String username, String status, int page, int pageSize);
    UserPost getById(Integer id);
    void approve(Integer id, Integer auditorId);
    void reject(Integer id, Integer auditorId, String reason);
    void delete(Integer id);
}