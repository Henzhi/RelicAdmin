package com.relic.aspect;

import com.relic.annotation.RequirePermission;
import com.relic.annotation.RequireRole;
import com.relic.constant.MessageConstant;
import com.relic.context.BaseContext;
import com.relic.exception.InsufficientPermissionsException;
import com.relic.service.PermissionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * 管理端权限校验切面：统一实现 {@link RequireRole} 与 {@link RequirePermission}。
 *
 * <p>替代原先散落在各 Service 中的手工角色检查（含未分配角色时的 NPE 风险），
 * 校验失败统一抛出 {@link InsufficientPermissionsException}，由全局异常处理器返回。</p>
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionCheckAspect {

    private final PermissionQueryService permissionQueryService;

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        Integer roleId = permissionQueryService.getCurrentAdminRoleId();
        boolean allowed = roleId != null
                && Arrays.stream(requireRole.value()).anyMatch(allowedRole -> allowedRole == roleId);
        if (!allowed) {
            log.warn("角色校验拒绝：method={}, required={}, actual={}",
                    joinPoint.getSignature().toShortString(), requireRole.value(), roleId);
            throw new InsufficientPermissionsException(MessageConstant.PERMISSION_DENIED);
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Long currentId = BaseContext.getCurrentId();
        if (permissionQueryService.isSuperAdmin(currentId)) {
            return joinPoint.proceed();
        }
        Set<String> ownedCodes = permissionQueryService.getPermissionCodes(permissionQueryService.getCurrentAdminRoleId());
        for (String required : requirePermission.value()) {
            if (ownedCodes.contains(required)) {
                return joinPoint.proceed();
            }
        }
        log.warn("权限校验拒绝：method={}, required={}, owned={}",
                joinPoint.getSignature().toShortString(), requirePermission.value(), ownedCodes);
        throw new InsufficientPermissionsException(MessageConstant.PERMISSION_DENIED);
    }
}
