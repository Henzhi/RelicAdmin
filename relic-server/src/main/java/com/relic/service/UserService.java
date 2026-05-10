package com.relic.service;

import com.relic.dto.*;
import com.relic.entity.User;

public interface UserService {
    PageDTO<User> page(String username, String nickname, String status, int page, int pageSize);
    User getById(Integer id);
    User getCurrentUser();
    void update(User user);
    void ban(Integer userId, UserBanDTO dto);
    void assignRoles(Integer userId, Integer[] roleIds);
    void disableComment(Integer userId, Integer commentDisabled);
    void disableUpload(Integer userId, Integer uploadDisabled);
    void updateAvatar(Integer userId, String avatarUrl);
}