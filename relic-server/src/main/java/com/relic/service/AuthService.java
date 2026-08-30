package com.relic.service;

import com.relic.dto.*;
import com.relic.vo.UserVO;

public interface AuthService {
    /** 登录并记录最后登录时间与来源IP */
    UserVO login(LoginDTO dto, String clientIp);
    UserVO register(RegisterDTO dto);
    void logout();
    void changePassword(Integer userId, PasswordChangeDTO dto);
    String generateToken(Integer userId, String username);
}