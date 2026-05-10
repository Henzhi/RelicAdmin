package com.relic.service;

import com.relic.dto.*;
import com.relic.vo.LoginVO;
import com.relic.vo.UserVO;

public interface AuthService {
    UserVO login(LoginDTO dto);
    UserVO register(RegisterDTO dto);
    void logout();
    void changePassword(Integer userId, PasswordChangeDTO dto);
    String generateToken(Integer userId, String username);
}