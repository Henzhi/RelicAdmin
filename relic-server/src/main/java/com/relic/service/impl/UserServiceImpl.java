package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.converter.VoConverter;
import com.relic.dto.UserBanDTO;
import com.relic.dto.UserCreateDTO;
import com.relic.dto.UserUpdateDTO;
import com.relic.entity.User;
import com.relic.exception.AccountNotFoundException;
import com.relic.mapper.UserMapper;
import com.relic.mapper.UserRoleMapper;
import com.relic.properties.JwtProperties;
import com.relic.service.UserService;
import com.relic.vo.PageResultVO;
import com.relic.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public PageResultVO<UserVO> page(String username, String nickname, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<User> entities = userMapper.selectByPage(username, nickname, status, offset, pageSize);
        long total = userMapper.countByPage(username, nickname, status);
        List<UserVO> records = entities.stream().map(VoConverter::toUserVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public UserVO getById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new AccountNotFoundException("账号不存在");
        }
        return VoConverter.toUserVO(user);
    }

    @Override
    public UserVO getCurrentUser() {
        Long id = BaseContext.getCurrentId();
        if (id == null) {
            throw new RuntimeException("用户未登录");
        }
        return getById(id.intValue());
    }

    @Override
    public void create(UserCreateDTO dto) {
        User existing = userMapper.selectByUsername(dto.getUsername());
        if (existing != null) {
            throw new RuntimeException("账号已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setSource(dto.getSource() != null ? dto.getSource() : "web");
        user.setStatus("active");
        user.setCommentDisabled(0);
        user.setUploadDisabled(0);
        LocalDateTime now = LocalDateTime.now();
        user.setRegisteredAt(now);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        userRoleMapper.deleteByUserId(id);
        userMapper.deleteById(id);
    }

    @Override
    public void update(UserUpdateDTO dto) {
        Long id = BaseContext.getCurrentId();
        User user = new User();
        user.setId(id.intValue());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
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
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            for (Integer roleId : roleIds) {
                userRoleMapper.insert(userId, roleId, 1, now);
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