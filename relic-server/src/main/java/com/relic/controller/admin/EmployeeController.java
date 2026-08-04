package com.relic.controller.admin;

import com.relic.dto.LoginDTO;
import com.relic.entity.AdminUser;
import com.relic.mapper.AdminUserMapper;
import com.relic.properties.JwtProperties;
import com.relic.result.Result;
import com.relic.service.LoginAttemptService;
import com.relic.utils.JwtUtil;
import com.relic.vo.AdminLoginVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        String username = dto.getUsername() == null ? "" : dto.getUsername();

        // H-01：IP 维度限流（防单 IP 高频尝试）
        if (loginAttemptService.isIpRateLimited(ip)) {
            return Result.error("尝试过于频繁，请稍后再试");
        }
        // H-01：账号+IP 锁定校验
        if (loginAttemptService.isLocked(username, ip)) {
            return Result.error("登录失败次数过多，账号已临时锁定，请 15 分钟后再试");
        }

        AdminUser adminUser = adminUserMapper.selectByUsername(username);
        if (adminUser == null) {
            loginAttemptService.recordFailure(username, ip);
            return Result.error("账号不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), adminUser.getPasswordHash())) {
            boolean locked = loginAttemptService.recordFailure(username, ip);
            return Result.error(locked ? "登录失败次数过多，账号已临时锁定，请 15 分钟后再试" : "用户名或密码错误");
        }
        if ("banned".equals(adminUser.getStatus())) {
            return Result.error("账号被锁定");
        }

        // 登录成功，清零失败计数
        loginAttemptService.reset(username, ip);

        adminUserMapper.updateLastLogin(adminUser.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                ip);

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
}