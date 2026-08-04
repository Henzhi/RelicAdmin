package com.relic.service;

import com.relic.mapper.SensitiveWordMapper;
import com.relic.properties.SensitiveWordCacheProperties;
import com.relic.service.impl.SensitiveWordChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * SensitiveWordChecker 单元测试（纯 Mock，不依赖 Redis/DB）。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SensitiveWordCheckerTest {

    @Mock
    private SensitiveWordMapper sensitiveWordMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private SensitiveWordCacheProperties cacheProperties;

    @InjectMocks
    private SensitiveWordChecker checker;

    @BeforeEach
    void setUp() {
        when(cacheProperties.isEnabled()).thenReturn(true);
        when(cacheProperties.getKeyPrefix()).thenReturn("relic:sensitive:words");
        when(cacheProperties.getTtlSeconds()).thenReturn(1800L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void nullOrBlankText_shouldReturnEmpty() {
        assertTrue(checker.checkText(null).isEmpty());
        assertTrue(checker.checkText("   ").isEmpty());
        verifyNoInteractions(sensitiveWordMapper);
    }

    @Test
    void hitWords_shouldReturnMatched() {
        when(valueOperations.get("relic:sensitive:words:enabled")).thenReturn(List.of("赌博", "暴力"));

        List<String> hits = checker.checkText("这是个包含赌博和暴力的测试文本");

        assertEquals(2, hits.size());
        assertTrue(hits.contains("赌博"));
        assertTrue(hits.contains("暴力"));
    }

    @Test
    void noHit_shouldReturnEmpty() {
        when(valueOperations.get("relic:sensitive:words:enabled")).thenReturn(List.of("赌博", "暴力"));

        assertTrue(checker.checkText("这是一个完全正常的文本").isEmpty());
    }

    @Test
    void cacheMiss_shouldFallbackToDatabase() {
        when(valueOperations.get("relic:sensitive:words:enabled")).thenReturn(null);
        when(sensitiveWordMapper.selectAllEnabledWords()).thenReturn(List.of("违禁词"));

        List<String> hits = checker.checkText("内容包含违禁词");

        verify(sensitiveWordMapper, times(1)).selectAllEnabledWords();
        // 回源后应写回缓存
        verify(valueOperations, times(1)).set(eq("relic:sensitive:words:enabled"),
                eq(List.of("违禁词")), anyLong(), any());
        assertTrue(hits.contains("违禁词"));
    }

    @Test
    void redisError_shouldDegradeToDatabase() {
        when(valueOperations.get("relic:sensitive:words:enabled"))
                .thenThrow(new RuntimeException("redis down"));
        when(sensitiveWordMapper.selectAllEnabledWords()).thenReturn(List.of("敏感"));

        List<String> hits = checker.checkText("文本里有敏感");

        assertTrue(hits.contains("敏感"));
    }
}
