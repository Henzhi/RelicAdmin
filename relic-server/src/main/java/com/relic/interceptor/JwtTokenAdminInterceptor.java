package com.relic.interceptor;

import com.relic.constant.JwtClaimsConstant;
import com.relic.context.BaseContext;
import com.relic.properties.JwtProperties;
import com.relic.service.SecurityLogService;
import com.relic.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired(required = false)
    private SecurityLogService securityLogService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getAdminTokenName());

        try {
            log.debug("jwt校验:{}", maskToken(token));
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);
            BaseContext.setCurrentId(userId);
            BaseContext.setCurrentUserType("admin");
            return true;
        } catch (Exception ex) {
            log.error("JWT校验失败：{}", ex.getMessage());
            String path = request.getRequestURI();
            if (path.contains("/login")) {
                try {
                    if (securityLogService != null) {
                        securityLogService.record(0L, "LOGIN_FAILED",
                                getClientIp(request), "管理员登录失败: " + ex.getMessage());
                    }
                } catch (Exception ignored) {}
            }
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        BaseContext.removeCurrentId();
    }

    /** 日志脱敏：仅展示 token 前 8 位，避免完整 JWT 落入日志 */
    private String maskToken(String token) {
        if (token == null) return "null";
        return token.length() > 8 ? token.substring(0, 8) + "..." : "***";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
