package com.relic.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成jwt
     *
     * @param secretKey jwt秘钥（字符串，长度至少32字符，建议使用Base64编码的长密钥）
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return JWT字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 生成安全密钥
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        // 计算过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)  // 自动使用 HS256 算法
                .setExpiration(exp)
                .compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥（与生成时使用的相同）
     * @param token     加密后的token
     * @return Claims
     */
    public static Claims parseJWT(String secretKey, String token) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}