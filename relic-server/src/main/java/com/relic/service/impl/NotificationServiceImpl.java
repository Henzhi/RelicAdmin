package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.NotificationReadDTO;
import com.relic.mapper.NotificationMapper;
import com.relic.service.NotificationService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public PageResultVO<Map<String, Object>> listUserNotifications(Integer isRead, int page, int pageSize) {
        Long currentUserId = BaseContext.getCurrentId();
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = notificationMapper.selectByUserId(currentUserId.intValue(), isRead, offset, pageSize);
        long total = notificationMapper.countByUserId(currentUserId.intValue(), isRead);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void markRead(NotificationReadDTO dto) {
        Long currentUserId = BaseContext.getCurrentId();
        if (dto.getIds() != null && dto.getIds().length > 0) {
            notificationMapper.markReadByIds(currentUserId.intValue(), dto.getIds());
            log.info("用户 {} 标记 {} 条通知已读", currentUserId, dto.getIds().length);
        } else {
            notificationMapper.markAllRead(currentUserId.intValue());
            log.info("用户 {} 标记全部通知已读", currentUserId);
        }
    }

    @Override
    public Map<String, Object> getUnreadCount() {
        Long currentUserId = BaseContext.getCurrentId();
        long count = notificationMapper.countUnread(currentUserId.intValue());
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return result;
    }
}