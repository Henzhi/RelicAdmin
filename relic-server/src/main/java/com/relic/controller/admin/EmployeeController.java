package com.relic.controller.admin;

import com.relic.constant.MessageConstant;
import com.relic.dto.LoginDTO;
import com.relic.entity.User;
import com.relic.mapper.UserMapper;
import com.relic.properties.JwtProperties;
import com.relic.result.Result;
import com.relic.utils.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@RequiredArgsConstructor
@Tag(name = "管理端认证", description = "管理员登录/退出")
public class EmployeeController {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            return Result.error("账号不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            return Result.error("密码错误");
        }
        if ("banned".equals(user.getStatus())) {
            return Result.error("账号被锁定");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(), claims);

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("token", token);
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}