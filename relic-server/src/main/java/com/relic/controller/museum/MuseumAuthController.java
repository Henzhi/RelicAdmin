package com.relic.controller.museum;

import com.relic.context.BaseContext;
import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.result.Result;
import com.relic.service.MuseumAuthService;
import com.relic.vo.LoginVO;
import com.relic.vo.UserVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/museum/auth")
@RequiredArgsConstructor
@Tag(name = "掌上博物馆端认证", description = "掌上博物馆用户注册、登录、信息管理")
public class MuseumAuthController {

    private final MuseumAuthService museumAuthService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        return Result.success(museumAuthService.register(dto));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        UserVO userVO = museumAuthService.login(dto);
        String token = museumAuthService.generateToken(userVO.getId(), userVO.getUsername());
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
        return Result.success(museumAuthService.getCurrentUser());
    }

    @PutMapping("/password")
    public Result<Void> password(@RequestBody PasswordChangeDTO dto) {
        Long userId = BaseContext.getCurrentId();
        museumAuthService.changePassword(userId.intValue(), dto);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        museumAuthService.logout();
        return Result.success();
    }
}