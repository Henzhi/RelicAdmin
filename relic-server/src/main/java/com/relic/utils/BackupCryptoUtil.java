package com.relic.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * M-12：备份文件 AES-256-GCM 加解密工具
 * <p>
 * 加密密钥由环境变量 {@code BACKUP_ENCRYPT_KEY} 注入（长度须 >= 32 字符）。
 * 未配置密钥时不启用加密（保持明文备份兼容），但会打印告警日志。
 * 加密文件格式：<12字节随机IV><密文>，IV 与密文同存。
 * </p>
 */
@Component
@Slf4j
public class BackupCryptoUtil {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    /** 加密后的文件后缀 */
    public static final String ENCRYPTED_SUFFIX = ".enc";

    private final String encryptionKey;

    public BackupCryptoUtil(@Value("${BACKUP_ENCRYPT_KEY:}") String encryptionKey) {
        this.encryptionKey = encryptionKey == null ? "" : encryptionKey.trim();
    }

    /**
     * 是否已启用加密（密钥已配置）
     */
    public boolean isEnabled() {
        return encryptionKey.length() >= 32;
    }

    /**
     * 判断文件是否为加密备份（后缀 .enc）
     */
    public boolean isEncrypted(File file) {
        return file != null && file.getName().endsWith(ENCRYPTED_SUFFIX);
    }

    /**
     * 加密文件：输入 -> 输出（输出路径 = 输入路径 + .enc）
     *
     * @return 加密后的文件；未启用加密或加密失败时返回原文件
     */
    public File encrypt(File sourceFile) {
        if (!isEnabled()) {
            log.warn("M-12: BACKUP_ENCRYPT_KEY 未配置（或长度<32），备份以明文存储！生产环境必须配置加密密钥");
            return sourceFile;
        }
        File target = new File(sourceFile.getAbsolutePath() + ENCRYPTED_SUFFIX);
        try {
            byte[] iv = new byte[IV_LENGTH];
            new java.security.SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, buildKey(), new GCMParameterSpec(TAG_LENGTH, iv));

            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(target);
                 CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                fos.write(iv);
                byte[] buf = new byte[8192];
                int len;
                while ((len = fis.read(buf)) != -1) {
                    cos.write(buf, 0, len);
                }
            }
            // 加密成功：删除明文文件
            sourceFile.delete();
            log.info("M-12: 备份文件已加密: {} -> {}", sourceFile.getAbsolutePath(), target.getAbsolutePath());
            return target;
        } catch (Exception e) {
            log.error("M-12: 备份加密失败，回退为明文存储: {}", e.getMessage(), e);
            target.delete();
            return sourceFile;
        }
    }

    /**
     * 解密文件（若为 .enc 后缀）：输出到同目录临时明文文件
     *
     * @return 解密后的明文文件；未启用加密或解密失败时返回原文件
     */
    public File decrypt(File encryptedFile) {
        if (!isEncrypted(encryptedFile)) {
            return encryptedFile;
        }
        if (!isEnabled()) {
            log.error("M-12: 备份文件为加密格式，但 BACKUP_ENCRYPT_KEY 未配置，无法解密");
            return encryptedFile;
        }
        String plainPath = encryptedFile.getAbsolutePath().substring(
                0, encryptedFile.getAbsolutePath().length() - ENCRYPTED_SUFFIX.length());
        File target = new File(plainPath);
        try {
            byte[] iv = new byte[IV_LENGTH];
            try (FileInputStream fis = new FileInputStream(encryptedFile)) {
                int read = 0;
                while (read < IV_LENGTH) {
                    int n = fis.read(iv, read, IV_LENGTH - read);
                    if (n == -1) throw new IOException("加密文件不完整");
                    read += n;
                }
            }
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, buildKey(), new GCMParameterSpec(TAG_LENGTH, iv));

            try (FileInputStream fis = new FileInputStream(encryptedFile);
                 FileOutputStream fos = new FileOutputStream(target);
                 CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                fis.skip(IV_LENGTH); // 跳过 IV，CipherInputStream 从密文开始读取
                byte[] buf = new byte[8192];
                int len;
                while ((len = cis.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
            }
            log.info("M-12: 备份文件已解密: {}", target.getAbsolutePath());
            return target;
        } catch (Exception e) {
            log.error("M-12: 备份解密失败（密钥错误或文件损坏）: {}", e.getMessage(), e);
            target.delete();
            return encryptedFile;
        }
    }

    private SecretKey buildKey() throws Exception {
        // 使用 SHA-256 派生 32 字节密钥，兼容任意长度的配置值
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = Arrays.copyOf(digest.digest(encryptionKey.getBytes(StandardCharsets.UTF_8)), 32);
        return new SecretKeySpec(keyBytes, "AES");
    }
}
