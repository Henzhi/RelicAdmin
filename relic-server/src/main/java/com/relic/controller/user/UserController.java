package com.relic.controller.user;

import com.relic.context.BaseContext;
import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.dto.UserUpdateDTO;
import com.relic.result.Result;
import com.relic.service.AuthService;
import com.relic.service.UserService;
import com.relic.vo.LoginVO;
import com.relic.vo.UserVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
@Tag(name = "用户端认证", description = "用户注册、登录、信息管理")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        UserVO userVO = authService.login(dto);
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