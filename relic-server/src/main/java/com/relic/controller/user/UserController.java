package com.relic.controller.user;

import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.entity.User;
import com.relic.result.Result;
import com.relic.service.AuthService;
import com.relic.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
@Tag(name = "用户端认证", description = "用户注册、登录、信息管理")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterDTO dto) {
        User user = authService.register(dto);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        return Result.success(data);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        User user = authService.login(dto);
        String token = authService.generateToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("token", token);
        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<User> info() {
        return Result.success(userService.getCurrentUser());
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody User user) {
        userService.update(user);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> password(@RequestBody PasswordChangeDTO dto) {
        Long userId = com.relic.context.BaseContext.getCurrentId();
        authService.changePassword(userId.intValue(), dto);
        return Result.success();
    }
}