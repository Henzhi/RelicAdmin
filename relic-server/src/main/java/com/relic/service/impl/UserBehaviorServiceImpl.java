package com.relic.service.impl;

import com.relic.entity.UserBehavior;
import com.relic.mapper.UserBehaviorMapper;
import com.relic.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBehaviorServiceImpl implements UserBehaviorService {

    private final UserBehaviorMapper userBehaviorMapper;

    @Override
    public void recordBehavior(Integer userId, String behaviorType, String targetType,
                               String targetId, String targetDesc, String detail) {
        try {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId);
            behavior.setBehaviorType(behaviorType);
            behavior.setTargetType(targetType);
            behavior.setTargetId(targetId);
            behavior.setTargetDesc(targetDesc);
            behavior.setDetail(detail);
            behavior.setCreatedAt(LocalDateTime.now());
            userBehaviorMapper.insert(behavior);
            log.info("用户行为记录: userId={}, type={}, target={}", userId, behaviorType, targetId);
        } catch (Exception e) {
            log.error("记录用户行为失败: userId={}, type={}, error={}", userId, behaviorType, e.getMessage());
        }
    }
}
