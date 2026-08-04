package com.relic.config;

import com.relic.properties.AliOssProperties;
import com.relic.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云文件上传配置
 */
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        // C-02：禁止打印完整对象（@Data toString 会输出 AK/SK），仅打印脱敏信息
        log.info("开始创建阿里云文件上传工具类对象：endpoint={}, bucket={}, accessKeyId={}",
                aliOssProperties.getEndpoint(),
                aliOssProperties.getBucketName(),
                mask(aliOssProperties.getAccessKeyId()));
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
     }

    /**
     * AccessKey 脱敏：仅保留前 4 位与后 4 位
     */
    private String mask(String key) {
        if (key == null || key.length() <= 8) {
            return "****";
        }
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }
}
