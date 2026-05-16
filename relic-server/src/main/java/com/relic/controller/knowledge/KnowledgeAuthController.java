package com.relic.controller.knowledge;

import com.relic.context.BaseContext;
import com.relic.dto.LoginDTO;
import com.relic.dto.PasswordChangeDTO;
import com.relic.dto.RegisterDTO;
import com.relic.dto.UserUpdateDTO;
import com.relic.result.Result;
import com.relic.service.KnowledgeAuthService;
import com.relic.vo.LoginVO;
import com.relic.vo.UserVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/knowledge/auth")
@RequiredArgsConstructor
@Tag(name = "知识服务端认证", description = "知识服务用户注册、登录、信息管理")
public class KnowledgeAuthController {

    private final KnowledgeAuthService knowledgeAuthService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        return Result.success(knowledgeAuthService.register(dto));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        UserVO userVO = knowledgeAuthService.login(dto);
        String token = knowledgeAuthService.generateToken(userVO.getId(), userVO.getUsername());
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
        return Result.success(knowledgeAuthService.getCurrentUser());
    }

    @PutMapping("/password")
    public Result<Void> password(@RequestBody PasswordChangeDTO dto) {
        Long userId = BaseContext.getCurrentId();
        knowledgeAuthService.changePassword(userId.intValue(), dto);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        knowledgeAuthService.logout();
        return Result.success();
    }
}