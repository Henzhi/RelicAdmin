package com.relic.service.impl;

import com.relic.mapper.SensitiveWordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensitiveWordChecker {

    private final SensitiveWordMapper sensitiveWordMapper;

    private volatile List<String> cachedWords;
    private volatile long lastLoadTime;
    private static final long CACHE_TTL = 5 * 60 * 1000;

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

    private List<String> getEnabledWords() {
        long now = System.currentTimeMillis();
        if (cachedWords == null || (now - lastLoadTime) > CACHE_TTL) {
            synchronized (this) {
                if (cachedWords == null || (now - lastLoadTime) > CACHE_TTL) {
                    cachedWords = sensitiveWordMapper.selectAllEnabledWords();
                    lastLoadTime = now;
                }
            }
        }
        return cachedWords != null ? cachedWords : Collections.emptyList();
    }

    public void clearCache() {
        cachedWords = null;
        lastLoadTime = 0;
    }
}