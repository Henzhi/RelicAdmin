package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.PageDTO;
import com.relic.dto.UserBanDTO;
import com.relic.entity.Role;
import com.relic.entity.User;
import com.relic.exception.AccountNotFoundException;
import com.relic.mapper.UserMapper;
import com.relic.mapper.UserRoleMapper;
import com.relic.properties.JwtProperties;
import com.relic.service.UserService;
import com.relic.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtProperties jwtProperties;

    @Override
    public PageDTO<User> page(String username, String nickname, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<User> records = userMapper.selectByPage(username, nickname, status, offset, pageSize);
        long total = userMapper.countByPage(username, nickname, status);
        records.forEach(u -> u.setPasswordHash(null));
        return new PageDTO<>(total, records, page, pageSize);
    }

    @Override
    public User getById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new AccountNotFoundException("账号不存在");
        }
        user.setPasswordHash(null);
        return user;
    }

    @Override
    public User getCurrentUser() {
        Long id = BaseContext.getCurrentId();
        if (id == null) {
            throw new RuntimeException("用户未登录");
        }
        return getById(id.intValue());
    }

    @Override
    public void update(User user) {
        Long id = BaseContext.getCurrentId();
        user.setId(id.intValue());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void ban(Integer userId, UserBanDTO dto) {
        userMapper.updateStatus(userId, dto.getStatus(), dto.getBanReason());
    }

    @Override
    @Transactional
    public void assignRoles(Integer userId, Integer[] roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && roleIds.length > 0) {
            for (Integer roleId : roleIds) {
                userRoleMapper.insert(userId, roleId, 1,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        }
    }

    @Override
    public void disableComment(Integer userId, Integer commentDisabled) {
        userMapper.updateCommentDisabled(userId, commentDisabled);
    }

    @Override
    public void disableUpload(Integer userId, Integer uploadDisabled) {
        userMapper.updateUploadDisabled(userId, uploadDisabled);
    }

    @Override
    public void updateAvatar(Integer userId, String avatarUrl) {
        userMapper.updateAvatar(userId, avatarUrl);
    }
}