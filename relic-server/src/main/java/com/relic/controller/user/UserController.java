package com.relic.controller.user;

import com.relic.context.BaseContext;
import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.dto.UserUpdateDTO;
import com.relic.result.Result;
import com.relic.service.AuthService;
import com.relic.service.LoginAttemptService;
import com.relic.service.UserService;
import com.relic.vo.LoginVO;
import com.relic.vo.UserVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
@Tag(name = "用户端认证", description = "用户注册、登录、信息管理")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        // H-01：登录防护（IP 限流 + 失败锁定）
        String ip = getClientIp(request);
        String username = dto.getUsername() == null ? "" : dto.getUsername();
        if (loginAttemptService.isIpRateLimited(ip)) {
            return Result.error("尝试过于频繁，请稍后再试");
        }
        if (loginAttemptService.isLocked(username, ip)) {
            return Result.error("登录失败次数过多，账号已临时锁定，请 15 分钟后再试");
        }
        UserVO userVO;
        try {
            userVO = authService.login(dto);
        } catch (RuntimeException e) {
            loginAttemptService.recordFailure(username, ip);
            throw e;
        }
        loginAttemptService.reset(username, ip);
        String token = authService.generateToken(userVO.getId(), userVO.getUsername());
        LoginVO loginVO = LoginVO.builder()
                .id(userVO.getId())
                .username(userVO.getUsername())
                .nickname(userVO.getNickname())
                .avatarUrl(userVO.getAvatarUrl())
                .userType(userVO.getUserType())
                .token(token)
                .build();
        return Result.success(loginVO);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    @GetMapping("/info")
    public Result<UserVO> info() {
        return Result.success(userService.getCurrentUser());
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody UserUpdateDTO dto) {
        userService.update(dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> password(@RequestBody PasswordChangeDTO dto) {
        Long userId = BaseContext.getCurrentId();
        authService.changePassword(userId.intValue(), dto);
        return Result.success();
    }
}