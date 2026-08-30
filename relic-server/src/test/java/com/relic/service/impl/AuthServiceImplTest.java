package com.relic.service.impl;

import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.entity.User;
import com.relic.exception.AccountAlreadyExistsException;
import com.relic.exception.AccountLockedException;
import com.relic.exception.PasswordEditFailedException;
import com.relic.exception.PasswordErrorException;
import com.relic.mapper.UserMapper;
import com.relic.properties.JwtProperties;
import com.relic.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户端认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl service;

    private User activeUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setUsername("alice");
        user.setPasswordHash("$2a$hash");
        user.setStatus("active");
        user.setNickname("Alice");
        return user;
    }

    private LoginDTO loginDto() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("alice");
        dto.setPassword("secret");
        return dto;
    }

    @Test
    void login_success_shouldRecordLastLoginWithIp() {
        User user = activeUser(5);
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("secret", "$2a$hash")).thenReturn(true);

        UserVO vo = service.login(loginDto(), "9.9.9.9");

        assertNotNull(vo);
        // 登录成功必须记录真实来源 IP（与管理端对齐）
        verify(userMapper).updateLastLogin(eq(5), anyString(), eq("9.9.9.9"));
    }

    @Test
    void login_bannedUser_shouldThrowAccountLocked() {
        User user = activeUser(5);
        user.setStatus("banned");
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("secret", "$2a$hash")).thenReturn(true);

        assertThrows(AccountLockedException.class, () -> service.login(loginDto(), "9.9.9.9"));
        verify(userMapper, never()).updateLastLogin(anyInt(), anyString(), anyString());
    }

    @Test
    void login_wrongPassword_shouldThrow() {
        User user = activeUser(5);
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("secret", "$2a$hash")).thenReturn(false);

        assertThrows(PasswordErrorException.class, () -> service.login(loginDto(), "9.9.9.9"));
    }

    @Test
    void register_duplicateUsername_shouldThrowAccountAlreadyExists() {
        when(userMapper.selectByUsername("alice")).thenReturn(activeUser(5));

        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("alice");
        dto.setPassword("secret");

        assertThrows(AccountAlreadyExistsException.class, () -> service.register(dto));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void changePassword_sameAsOld_shouldThrow() {
        User user = activeUser(5);
        when(userMapper.selectById(5)).thenReturn(user);
        when(passwordEncoder.matches("old", "$2a$hash")).thenReturn(true);

        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setOldPassword("old");
        dto.setNewPassword("old");

        assertThrows(PasswordEditFailedException.class, () -> service.changePassword(5, dto));
        verify(userMapper, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    void changePassword_wrongOldPassword_shouldThrow() {
        User user = activeUser(5);
        when(userMapper.selectById(5)).thenReturn(user);
        when(passwordEncoder.matches("wrong", "$2a$hash")).thenReturn(false);

        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setOldPassword("wrong");
        dto.setNewPassword("new");

        assertThrows(PasswordErrorException.class, () -> service.changePassword(5, dto));
    }

    @Test
    void changePassword_success_shouldUpdateHash() {
        User user = activeUser(5);
        when(userMapper.selectById(5)).thenReturn(user);
        when(passwordEncoder.matches("old", "$2a$hash")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("$2a$new");

        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        assertDoesNotThrow(() -> service.changePassword(5, dto));
        verify(userMapper).updatePassword(5, "$2a$new");
    }
}
