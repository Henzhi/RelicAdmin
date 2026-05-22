package com.relic.aspect;

import com.relic.annotation.OperationLog;
import com.relic.context.BaseContext;
import com.relic.mapper.OperationLogMapper;
import com.relic.mapper.SystemLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final SystemLogMapper systemLogMapper;

    @AfterReturning(pointcut = "@annotation(com.relic.annotation.OperationLog)")
    public void afterReturning(JoinPoint joinPoint) {
        recordLog(joinPoint, "success");
    }

    @AfterThrowing(pointcut = "@annotation(com.relic.annotation.OperationLog)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        recordLog(joinPoint, "failed:" + e.getMessage());
    }

    private void recordLog(JoinPoint joinPoint, String result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog annotation = method.getAnnotation(OperationLog.class);

            Long userId = getCurrentUserId();
            String operationType = annotation.operationType();
            String targetType = annotation.targetType();
            if (targetType.isEmpty()) {
                targetType = signature.getDeclaringType().getSimpleName().replace("AdminController", "");
            }

            String ip = "";
            String userAgent = "";
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                ip = getClientIp(request);
                userAgent = request.getHeader("User-Agent");
            }

            StringBuilder newValue = new StringBuilder();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg != null && !(arg instanceof HttpServletRequest)) {
                    if (newValue.length() > 0) newValue.append(", ");
                    String val = arg.toString();
                    if (val.length() > 500) val = val.substring(0, 500) + "...";
                    newValue.append(val);
                }
            }

            operationLogMapper.insert(userId, operationType, targetType, "",
                    null, newValue.toString(), ip, userAgent);
            log.info("[OperationLog] userId={}, type={}, target={}, result={}",
                    userId, operationType, targetType, result);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
            try {
                systemLogMapper.insert("ERROR", "LogAspect",
                        "记录操作日志时发生异常", e.getMessage());
            } catch (Exception ignored) {}
        }
    }

    private Long getCurrentUserId() {
        try {
            Object userId = BaseContext.getCurrentId();
            if (userId instanceof Long) return (Long) userId;
            if (userId instanceof Integer) return ((Integer) userId).longValue();
        } catch (Exception ignored) {}
        return 0L;
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