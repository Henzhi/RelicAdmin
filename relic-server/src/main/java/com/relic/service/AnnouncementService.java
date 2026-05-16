package com.relic.service;

import com.relic.dto.AnnouncementCreateDTO;
import com.relic.vo.PageResultVO;

import java.util.List;
import java.util.Map;

public interface AnnouncementService {
    PageResultVO<Map<String, Object>> listAdminAnnouncements(Integer status, int page, int pageSize);
    List<Map<String, Object>> listActiveAnnouncements(String position);
    void create(AnnouncementCreateDTO dto);
    void update(Long id, AnnouncementCreateDTO dto);
    void offline(Long id);
    void delete(Long id);
}