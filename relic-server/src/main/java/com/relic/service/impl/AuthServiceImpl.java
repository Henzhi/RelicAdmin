package com.relic.service.impl;

import com.relic.constant.MessageConstant;
import com.relic.context.BaseContext;
import com.relic.converter.VoConverter;
import com.relic.dto.*;
import com.relic.entity.Role;
import com.relic.entity.User;
import com.relic.exception.AccountNotFoundException;
import com.relic.exception.PasswordEditFailedException;
import com.relic.exception.PasswordErrorException;
import com.relic.mapper.UserMapper;
import com.relic.properties.JwtProperties;
import com.relic.service.AuthService;
import com.relic.utils.JwtUtil;
import com.relic.vo.LoginVO;
import com.relic.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserVO login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new AccountNotFoundException("账号不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new PasswordErrorException("密码错误");
        }
        if (Boolean.TRUE.equals("banned".equals(user.getStatus()))) {
            throw new AccountNotFoundException("账号被锁定");
        }
        userMapper.updateLastLogin(user.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "");
        return VoConverter.toUserVO(user);
    }

    @Override
    public UserVO register(RegisterDTO dto) {
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
        user.setUserType(dto.getUserType() != null ? dto.getUserType() : "knowledge");
        user.setStatus("active");
        user.setCommentDisabled(0);
        user.setUploadDisabled(0);
        LocalDateTime now = LocalDateTime.now();
        user.setRegisteredAt(now);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);
        return VoConverter.toUserVO(user);
    }

    @Override
    public void logout() {
        BaseContext.removeCurrentId();
    }

    @Override
    public void changePassword(Integer userId, PasswordChangeDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new AccountNotFoundException("账号不存在");
        }
        if (dto.getOldPassword() != null && dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_SAME_AS_OLD);
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new PasswordErrorException("密码错误");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(dto.getNewPassword()));
    }

    @Override
    public String generateToken(Integer userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("username", username);
        claims.put("user_type", "knowledge");
        return JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
    }
}