package com.relic.service;

public interface UserBehaviorService {
    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param behaviorType 行为类型：publish/comment/upload/like/favorite/follow/browse
     * @param targetType 目标类型：artifact/post/comment/user
     * @param targetId 目标对象ID
     * @param targetDesc 目标描述
     * @param detail 行为详情（JSON）
     */
    void recordBehavior(Integer userId, String behaviorType, String targetType,
                        String targetId, String targetDesc, String detail);
}
