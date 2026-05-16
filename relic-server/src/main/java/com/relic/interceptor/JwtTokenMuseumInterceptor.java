package com.relic.interceptor;

import com.relic.constant.JwtClaimsConstant;
import com.relic.context.BaseContext;
import com.relic.properties.JwtProperties;
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
public class JwtTokenMuseumInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getMuseumTokenName());

        try {
            log.info("掌上博物馆jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getMuseumSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            String userType = claims.get(JwtClaimsConstant.USER_TYPE, String.class);
            log.info("当前掌上博物馆用户id：{}, 类型：{}", userId, userType);
            BaseContext.setCurrentId(userId);
            BaseContext.setCurrentUserType(userType);
            return true;
        } catch (Exception ex) {
            log.error("掌上博物馆JWT校验失败：{}", ex.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}