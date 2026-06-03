package com.relic.controller.admin;

import com.relic.dto.LoginDTO;
import com.relic.entity.AdminUser;
import com.relic.mapper.AdminUserMapper;
import com.relic.properties.JwtProperties;
import com.relic.result.Result;
import com.relic.utils.JwtUtil;
import com.relic.vo.AdminLoginVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@RequiredArgsConstructor
@Tag(name = "管理端认证", description = "管理员登录/退出")
public class EmployeeController {

    private final AdminUserMapper adminUserMapper;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody LoginDTO dto) {
        AdminUser adminUser = adminUserMapper.selectByUsername(dto.getUsername());
        if (adminUser == null) {
            return Result.error("账号不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), adminUser.getPasswordHash())) {
            return Result.error("用户名或密码错误");
        }
        if ("banned".equals(adminUser.getStatus())) {
            return Result.error("账号被锁定");
        }

        adminUserMapper.updateLastLogin(adminUser.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "");

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", adminUser.getId());
        claims.put("username", adminUser.getUsername());
        claims.put("user_type", "admin");
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(), claims);

        AdminLoginVO vo = AdminLoginVO.builder()
                .id(adminUser.getId())
                .username(adminUser.getUsername())
                .realName(adminUser.getRealName())
                .avatarUrl(adminUser.getAvatarUrl())
                .token(token)
                .build();
        return Result.success(vo);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}