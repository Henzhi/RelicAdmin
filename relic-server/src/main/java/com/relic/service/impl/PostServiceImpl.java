package com.relic.service.impl;

import com.relic.entity.UserPost;
import com.relic.exception.AccountNotFoundException;
import com.relic.mapper.PostMapper;
import com.relic.service.PostService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    public PageResultVO<UserPost> page(String username, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserPost> records = postMapper.selectByPage(username, status, offset, pageSize);
        long total = postMapper.countByPage(username, status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public UserPost getById(Integer id) {
        UserPost post = postMapper.selectById(id);
        if (post == null) {
            throw new AccountNotFoundException("动态不存在");
        }
        return post;
    }

    @Override
    public void approve(Integer id, Integer auditorId) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        postMapper.updateStatus(id, "approved", auditorId, now, null);
    }

    @Override
    public void reject(Integer id, Integer auditorId, String reason) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        postMapper.updateStatus(id, "rejected", auditorId, now, reason);
    }

    @Override
    public void delete(Integer id) {
        postMapper.deleteById(id);
    }
}