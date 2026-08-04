package com.relic.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试：验证 token 生成/解析/过期/篡改防护。
 */
class JwtUtilTest {

    /** 32 字节 Base64 密钥（HS256 合法长度） */
    private static final String SECRET = "QjnEXgrR7DaaP2quggUJrQED2hVCKVHUcKaHRdMGHdU=";

    @Test
    void createAndParseJwt_shouldRoundTrip() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", 10086L);
        claims.put("user_type", "admin");

        String token = JwtUtil.createJWT(SECRET, 60_000L, claims);
        assertNotNull(token);
        assertFalse(token.isBlank());

        Claims parsed = JwtUtil.parseJWT(SECRET, token);
        assertEquals(10086L, Long.valueOf(parsed.get("user_id", Long.class)));
        assertEquals("admin", parsed.get("user_type", String.class));
    }

    @Test
    void parseExpiredToken_shouldThrow() {
        String token = JwtUtil.createJWT(SECRET, -1000L, Map.of("user_id", 1L));
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> JwtUtil.parseJWT(SECRET, token));
    }

    @Test
    void parseWithWrongSecret_shouldThrow() {
        String token = JwtUtil.createJWT(SECRET, 60_000L, Map.of("user_id", 1L));
        assertThrows(io.jsonwebtoken.security.SignatureException.class,
                () -> JwtUtil.parseJWT("aGVsbG8gd29ybGQgMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3", token));
    }

    @Test
    void parseTamperedToken_shouldThrow() {
        String token = JwtUtil.createJWT(SECRET, 60_000L, Map.of("user_id", 1L));
        String tampered = token.substring(0, token.length() - 2) + "xx";
        assertThrows(io.jsonwebtoken.JwtException.class,
                () -> JwtUtil.parseJWT(SECRET, tampered));
    }

    @Test
    void invalidSecretLength_shouldThrowWeakKey() {
        // 24 字节密钥对 HS256 不足，应抛出 WeakKeyException（防 BUG-007 回归）
        String shortSecret = "kR8vN2mP5xL9wQ3jF7tY1bD6hA0cE4gI";
        assertThrows(io.jsonwebtoken.security.WeakKeyException.class,
                () -> JwtUtil.createJWT(shortSecret, 60_000L, Map.of("user_id", 1L)));
    }
}
