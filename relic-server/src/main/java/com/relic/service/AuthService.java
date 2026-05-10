package com.relic.service;

import com.relic.dto.*;
import com.relic.entity.User;

public interface AuthService {
    User login(LoginDTO dto);
    User register(RegisterDTO dto);
    void logout();
    void changePassword(Integer userId, PasswordChangeDTO dto);
    String generateToken(User user);
}