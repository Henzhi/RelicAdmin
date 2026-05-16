package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.AnnouncementCreateDTO;
import com.relic.mapper.AnnouncementMapper;
import com.relic.service.AnnouncementService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public PageResultVO<Map<String, Object>> listAdminAnnouncements(Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = announcementMapper.selectAdminPage(status, offset, pageSize);
        long total = announcementMapper.countAdminPage(status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public List<Map<String, Object>> listActiveAnnouncements(String position) {
        return announcementMapper.selectActiveList(position);
    }

    @Override
    public void create(AnnouncementCreateDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("公告标题不能为空");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("公告内容不能为空");
        }
        Long createdBy = BaseContext.getCurrentId();
        announcementMapper.insert(dto.getTitle(), dto.getContent(),
                dto.getPosition() != null ? dto.getPosition() : "global",
                dto.getTargetAudience() != null ? dto.getTargetAudience() : "all",
                dto.getStartTime(), dto.getEndTime(),
                dto.getStatus() != null ? dto.getStatus() : 1,
                createdBy);
        log.info("管理员 {} 创建了公告: {}", createdBy, dto.getTitle());
    }

    @Override
    public void update(Long id, AnnouncementCreateDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("公告标题不能为空");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("公告内容不能为空");
        }
        announcementMapper.update(id, dto.getTitle(), dto.getContent(),
                dto.getPosition() != null ? dto.getPosition() : "global",
                dto.getTargetAudience() != null ? dto.getTargetAudience() : "all",
                dto.getStartTime(), dto.getEndTime(),
                dto.getStatus() != null ? dto.getStatus() : 1);
        log.info("公告 {} 已更新", id);
    }

    @Override
    public void offline(Long id) {
        announcementMapper.updateStatus(id, 0);
        log.info("公告 {} 已下线", id);
    }

    @Override
    public void delete(Long id) {
        announcementMapper.deleteById(id);
        log.info("公告 {} 已删除", id);
    }
}