package com.relic.service;

import com.relic.dto.*;
import com.relic.vo.PageResultVO;
import com.relic.vo.UserVO;
import com.relic.dto.UserCreateDTO;

public interface UserService {
    PageResultVO<UserVO> page(String username, String nickname, String status, int page, int pageSize);
    UserVO getById(Integer id);
    UserVO getCurrentUser();
    void create(UserCreateDTO dto);
    void update(UserUpdateDTO dto);
    void delete(Integer id);
    void ban(Integer userId, UserBanDTO dto);
    void assignRoles(Integer userId, Integer[] roleIds);
    void disableComment(Integer userId, Integer commentDisabled);
    void disableUpload(Integer userId, Integer uploadDisabled);
    void updateAvatar(Integer userId, String avatarUrl);
}