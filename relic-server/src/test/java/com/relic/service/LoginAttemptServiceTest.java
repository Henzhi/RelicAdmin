package com.relic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * H-01：登录防护逻辑单元测试（纯 Mock，不依赖真实 Redis）
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginAttemptServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService(stringRedisTemplate);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void notLocked_initialState() {
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(false);
        assertFalse(service.isLocked("admin", "1.2.3.4"));
    }

    @Test
    void locked_whenKeyExists() {
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(true);
        assertTrue(service.isLocked("admin", "1.2.3.4"));
    }

    @Test
    void recordFailure_underThreshold_notLocked() {
        when(valueOperations.increment(anyString())).thenReturn(3L);
        assertFalse(service.recordFailure("admin", "1.2.3.4"));
        // 未达到阈值，不写入锁定 key（验证 valueOperations.set 从未以锁定 key 调用）
        verify(valueOperations, never()).set(eq("relic:login:lock:admin:1.2.3.4"), anyString(), any(Duration.class));
    }

    @Test
    void recordFailure_reachThreshold_locked() {
        when(valueOperations.increment(anyString())).thenReturn(5L);
        assertTrue(service.recordFailure("admin", "1.2.3.4"));
        verify(valueOperations).set(eq("relic:login:lock:admin:1.2.3.4"), eq("1"), any(Duration.class));
    }

    @Test
    void reset_clearsFailAndLockKeys() {
        service.reset("admin", "1.2.3.4");
        verify(stringRedisTemplate).delete("relic:login:fail:admin:1.2.3.4");
        verify(stringRedisTemplate).delete("relic:login:lock:admin:1.2.3.4");
    }

    @Test
    void ipRateLimited_whenExceedsLimit() {
        // 第一次调用（计数1）不触发
        when(valueOperations.increment(anyString())).thenReturn(1L);
        assertFalse(service.isIpRateLimited("9.9.9.9"));
        // 超过阈值（21 次）触发限流
        when(valueOperations.increment(anyString())).thenReturn(21L);
        assertTrue(service.isIpRateLimited("9.9.9.9"));
    }
}
