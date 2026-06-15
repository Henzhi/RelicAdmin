package com.relic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "relic.cache.sensitive-word")
public class SensitiveWordCacheProperties {

    /**
     * Redis 缓存 key 前缀
     */
    private String keyPrefix = "relic:sensitive:words";

    /**
     * 缓存过期时间，默认30分钟
     */
    private String ttl = "30m";

    /**
     * 是否启用 Redis 缓存，默认启用
     */
    private boolean enabled = true;

    /**
     * 解析 TTL 为秒数
     */
    public long getTtlSeconds() {
        String ttlStr = this.ttl.trim().toLowerCase();
        if (ttlStr.endsWith("m")) {
            return Long.parseLong(ttlStr.replace("m", "")) * 60;
        } else if (ttlStr.endsWith("h")) {
            return Long.parseLong(ttlStr.replace("h", "")) * 3600;
        } else if (ttlStr.endsWith("s")) {
            return Long.parseLong(ttlStr.replace("s", ""));
        }
        return Long.parseLong(ttlStr);
    }
}
