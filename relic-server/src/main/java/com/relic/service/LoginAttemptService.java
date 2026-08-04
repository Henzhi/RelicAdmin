package com.relic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * H-01 登录防护：基于 Redis 的失败次数计数与账号/IP 锁定
 * <p>
 * 策略：
 * - 同一「账号+IP」连续失败 MAX_ATTEMPTS 次，锁定 LOCK_MINUTES 分钟；
 * - 同一 IP 的瞬时失败计数超过阈值时，也按 IP 维度临时限流；
 * - 登录成功后清零计数。
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {

    private final StringRedisTemplate stringRedisTemplate;

    /** 允许的最大连续失败次数 */
    private static final int MAX_ATTEMPTS = 5;
    /** 锁定时长（分钟） */
    private static final long LOCK_MINUTES = 15;

    private static final String KEY_FAIL = "relic:login:fail:";
    private static final String KEY_LOCK = "relic:login:lock:";

    /**
     * 判断指定账号+IP 是否已被锁定
     */
    public boolean isLocked(String username, String ip) {
        Boolean locked = stringRedisTemplate.hasKey(KEY_LOCK + username + ":" + ip);
        return Boolean.TRUE.equals(locked);
    }

    /**
     * 记录一次登录失败；达到阈值时自动锁定并返回 true
     */
    public boolean recordFailure(String username, String ip) {
        String failKey = KEY_FAIL + username + ":" + ip;
        Long count = stringRedisTemplate.opsForValue().increment(failKey);
        if (count == null) {
            count = 1L;
        }
        if (count == 1) {
            // 首次失败开始计时窗口，防止长期占用 key
            stringRedisTemplate.expire(failKey, Duration.ofMinutes(LOCK_MINUTES));
        }
        if (count >= MAX_ATTEMPTS) {
            stringRedisTemplate.opsForValue().set(KEY_LOCK + username + ":" + ip, "1", Duration.ofMinutes(LOCK_MINUTES));
            stringRedisTemplate.delete(failKey);
            log.warn("登录防护：账号 {}（IP {}）连续失败 {} 次，已锁定 {} 分钟", username, ip, count, LOCK_MINUTES);
            return true;
        }
        return false;
    }

    /**
     * 登录成功后清零失败计数
     */
    public void reset(String username, String ip) {
        stringRedisTemplate.delete(KEY_FAIL + username + ":" + ip);
        stringRedisTemplate.delete(KEY_LOCK + username + ":" + ip);
    }

    /**
     * 获取 IP 维度限流判断（简单滑动窗口：同 IP 每分钟最多 MAX_IP_PER_MINUTE 次尝试）
     */
    public boolean isIpRateLimited(String ip) {
        String ipKey = "relic:login:ip:" + ip;
        Long count = stringRedisTemplate.opsForValue().increment(ipKey);
        if (count == null) {
            count = 1L;
        }
        if (count == 1) {
            stringRedisTemplate.expire(ipKey, Duration.ofMinutes(1));
        }
        // 同 IP 每分钟超过 20 次登录尝试则限流
        return count > 20;
    }
}
