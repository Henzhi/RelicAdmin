package com.relic.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * M-01：安全响应头过滤器（CSP / X-Frame-Options / X-Content-Type-Options / Referrer-Policy）
 * <p>缓解 XSS 与点击劫持风险。生产环境如需更细粒度 CSP，可在 nginx 层覆盖。</p>
 */
@Component
@Order(-100)
@Slf4j
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // CSP：仅允许同源与显式白名单资源；阻止内联脚本执行（部分页面需联调时按需放宽）
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; " +
                "font-src 'self' data:; connect-src 'self' https:; frame-ancestors 'self'");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        filterChain.doFilter(request, response);
    }
}
