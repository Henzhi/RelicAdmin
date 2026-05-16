package com.relic.service.impl;

import com.relic.entity.UserComment;
import com.relic.exception.AccountNotFoundException;
import com.relic.mapper.CommentMapper;
import com.relic.service.CommentService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

    @Override
    public PageResultVO<UserComment> page(String username, String status, Integer artifactId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserComment> records = commentMapper.selectByPage(username, status, artifactId, offset, pageSize);
        long total = commentMapper.countByPage(username, status, artifactId);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public UserComment getById(Integer id) {
        UserComment comment = commentMapper.selectById(id);
        if (comment == null) throw new AccountNotFoundException("评论不存在");
        return comment;
    }

    @Override
    public void approve(Integer id, Integer auditorId) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        commentMapper.updateStatus(id, "approved", auditorId, now, null);
    }

    @Override
    public void reject(Integer id, Integer auditorId, String reason) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        commentMapper.updateStatus(id, "rejected", auditorId, now, reason);
    }

    @Override
    public void delete(Integer id) {
        commentMapper.deleteById(id);
    }
}