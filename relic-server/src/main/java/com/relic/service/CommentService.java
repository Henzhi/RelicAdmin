package com.relic.service;

import com.relic.entity.UserComment;
import com.relic.vo.PageResultVO;

public interface CommentService {
    PageResultVO<UserComment> page(String username, String status, Integer artifactId, int page, int pageSize);
    UserComment getById(Integer id);
    void approve(Integer id, Integer auditorId);
    void reject(Integer id, Integer auditorId, String reason);
    void delete(Integer id);
}