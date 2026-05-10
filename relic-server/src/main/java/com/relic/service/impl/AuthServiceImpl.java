package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.entity.User;
import com.relic.exception.AccountNotFoundException;
import com.relic.exception.PasswordErrorException;
import com.relic.mapper.UserMapper;
import com.relic.properties.JwtProperties;
import com.relic.service.AuthService;
import com.relic.utils.JwtUtil;
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
    public User login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new AccountNotFoundException("账号不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new PasswordErrorException("密码错误");
        }
        if ("banned".equals(user.getStatus())) {
            throw new AccountNotFoundException("账号被锁定");
        }
        userMapper.updateLastLogin(user.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "");
        user.setPasswordHash(null);
        return user;
    }

    @Override
    public User register(RegisterDTO dto) {
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
        user.setPasswordHash(null);
        return user;
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
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new PasswordErrorException("密码错误");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(dto.getNewPassword()));
    }

    @Override
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("username", user.getUsername());
        return JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
    }
}