package com.relic;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

/**
 * JWT 密钥生成工具（仅开发/部署时手动运行，勿部署到生产包路径）
 *
 * <p>用法：直接运行 main 方法，输出 HS256 所需的 44 字符 Base64 密钥（32 字节），
 * 填入 application.yml 的 relic.jwt.*-secret-key 配置。</p>
 */
public class KeyGenerator {

    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Secret = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("通用密钥: " + base64Secret);

        SecretKey adminKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println("Admin Base64: " + Encoders.BASE64.encode(adminKey.getEncoded()));

        SecretKey userKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println("User Base64: " + Encoders.BASE64.encode(userKey.getEncoded()));
    }
}
