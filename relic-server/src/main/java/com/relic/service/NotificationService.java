package com.relic.service;

import com.relic.dto.NotificationReadDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface NotificationService {
    PageResultVO<Map<String, Object>> listUserNotifications(Integer isRead, int page, int pageSize);
    void markRead(NotificationReadDTO dto);
    Map<String, Object> getUnreadCount();
}