package com.relic.service.impl;

import com.relic.entity.UserFollow;
import com.relic.exception.AccountNotFoundException;
import com.relic.mapper.FollowMapper;
import com.relic.mapper.UserMapper;
import com.relic.service.FollowService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @Override
    public PageResultVO<UserFollow> page(String followerName, String followeeName, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserFollow> records = followMapper.selectByPage(followerName, followeeName, offset, pageSize);
        long total = followMapper.countByPage(followerName, followeeName);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public UserFollow getByFollowerAndFollowee(Integer followerId, Integer followeeId) {
        return followMapper.selectByFollowerAndFollowee(followerId, followeeId);
    }

    @Override
    @Transactional
    public void follow(Integer followerId, Integer followeeId) {
        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("不能关注自己");
        }
        if (userMapper.selectById(followeeId) == null) {
            throw new AccountNotFoundException("被关注的用户不存在");
        }
        UserFollow existing = followMapper.selectByFollowerAndFollowee(followerId, followeeId);
        if (existing != null) {
            return;
        }
        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFolloweeId(followeeId);
        follow.setCreatedAt(LocalDateTime.now());
        followMapper.insert(follow);
    }

    @Override
    @Transactional
    public void unfollow(Integer followerId, Integer followeeId) {
        followMapper.delete(followerId, followeeId);
    }
}