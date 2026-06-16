package com.relic.service.impl;

import com.relic.mapper.SensitiveWordMapper;
import com.relic.properties.SensitiveWordCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensitiveWordChecker {

    private final SensitiveWordMapper sensitiveWordMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SensitiveWordCacheProperties cacheProperties;

    /**
     * Redis 中敏感词列表的 key
     */
    private String getCacheKey() {
        return cacheProperties.getKeyPrefix() + ":enabled";
    }

    /**
     * 检查文本中是否包含敏感词，返回命中的敏感词列表
     */
    @SuppressWarnings("unchecked")
    public List<String> checkText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<String> words = getEnabledWords();
        List<String> hits = new ArrayList<>();
        for (String word : words) {
            if (text.contains(word)) {
                hits.add(word);
            }
        }
        return hits;
    }

    /**
     * 获取启用的敏感词列表，优先从 Redis 缓存读取
     */
    @SuppressWarnings("unchecked")
    private List<String> getEnabledWords() {
        if (!cacheProperties.isEnabled()) {
            return loadFromDatabase();
        }

        String cacheKey = getCacheKey();
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                if (cached instanceof List) {
                    return (List<String>) cached;
                }
            }
        } catch (Exception e) {
            log.warn("Redis 读取敏感词缓存失败，降级到数据库查询: {}", e.getMessage());
        }

        List<String> words = loadFromDatabase();
        saveToCache(words);
        return words;
    }

    /**
     * 从数据库加载启用的敏感词
     */
    private List<String> loadFromDatabase() {
        List<String> words = sensitiveWordMapper.selectAllEnabledWords();
        return words != null ? words : Collections.emptyList();
    }

    /**
     * 将敏感词列表写入 Redis 缓存
     */
    private void saveToCache(List<String> words) {
        if (!cacheProperties.isEnabled()) {
            return;
        }
        try {
            String cacheKey = getCacheKey();
            long ttlSeconds = cacheProperties.getTtlSeconds();
            redisTemplate.opsForValue().set(cacheKey, words, ttlSeconds, TimeUnit.SECONDS);
            log.debug("敏感词缓存已写入 Redis，key={}，TTL={}s，词数={}", cacheKey, ttlSeconds, words.size());
        } catch (Exception e) {
            log.warn("Redis 写入敏感词缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 清除 Redis 缓存，下次查询时自动从数据库重新加载
     */
    public void clearCache() {
        if (!cacheProperties.isEnabled()) {
            return;
        }
        try {
            String cacheKey = getCacheKey();
            redisTemplate.delete(cacheKey);
            log.info("敏感词 Redis 缓存已清除，key={}", cacheKey);
        } catch (Exception e) {
            log.warn("Redis 清除敏感词缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 主动刷新缓存：从数据库重新加载并写入 Redis
     */
    public void refreshCache() {
        List<String> words = loadFromDatabase();
        saveToCache(words);
        log.info("敏感词缓存已刷新，词数={}", words.size());
    }
}
