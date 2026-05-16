package com.relic.service;

import com.relic.entity.UserFollow;
import com.relic.vo.PageResultVO;

public interface FollowService {
    PageResultVO<UserFollow> page(String followerName, String followeeName, int page, int pageSize);
    UserFollow getByFollowerAndFollowee(Integer followerId, Integer followeeId);
    void follow(Integer followerId, Integer followeeId);
    void unfollow(Integer followerId, Integer followeeId);
}