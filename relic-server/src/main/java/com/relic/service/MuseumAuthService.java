package com.relic.service;

import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.vo.LoginVO;
import com.relic.vo.UserVO;

public interface MuseumAuthService {
    UserVO login(LoginDTO dto);
    UserVO register(RegisterDTO dto);
    void logout();
    void changePassword(Integer userId, PasswordChangeDTO dto);
    String generateToken(Integer userId, String username);
    UserVO getCurrentUser();
}