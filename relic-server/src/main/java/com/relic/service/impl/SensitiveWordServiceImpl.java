package com.relic.service.impl;

import com.relic.dto.SensitiveWordCreateDTO;
import com.relic.dto.SensitiveWordUpdateDTO;
import com.relic.exception.SensitiveWordException;
import com.relic.mapper.SensitiveWordMapper;
import com.relic.service.SensitiveWordService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensitiveWordServiceImpl implements SensitiveWordService {

    private final SensitiveWordMapper sensitiveWordMapper;
    private final SensitiveWordChecker sensitiveWordChecker;

    @Override
    public PageResultVO<Map<String, Object>> page(String word, String category, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = sensitiveWordMapper.selectByPage(word, category, status, offset, pageSize);
        long total = sensitiveWordMapper.countByPage(word, category, status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void create(SensitiveWordCreateDTO dto) {
        if (dto.getWord() == null || dto.getWord().isBlank()) {
            throw new IllegalArgumentException("敏感词不能为空");
        }
        String word = dto.getWord().trim();
        if (sensitiveWordMapper.checkExists(word) > 0) {
            throw new SensitiveWordException("敏感词已存在");
        }
        sensitiveWordMapper.insert(word, dto.getCategory() != null ? dto.getCategory() : "other");
        sensitiveWordChecker.clearCache();
        log.info("新增敏感词: {}", word);
    }

    @Override
    public void update(Long id, SensitiveWordUpdateDTO dto) {
        if (dto.getWord() == null || dto.getWord().isBlank()) {
            throw new IllegalArgumentException("敏感词不能为空");
        }
        String word = dto.getWord().trim();
        int status = dto.getStatus() != null ? dto.getStatus() : 1;
        sensitiveWordMapper.update(id, word, dto.getCategory() != null ? dto.getCategory() : "other", status);
        sensitiveWordChecker.clearCache();
        log.info("更新敏感词 id={}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        sensitiveWordMapper.updateStatus(id, status);
        sensitiveWordChecker.clearCache();
        log.info("敏感词 id={} 状态更新为 {}", id, status);
    }

    @Override
    public void delete(Long id) {
        sensitiveWordMapper.deleteById(id);
        sensitiveWordChecker.clearCache();
        log.info("删除敏感词 id={}", id);
    }

    @Override
    @Transactional
    public void batchImport(String content) {
        String[] lines = content.split("\\r?\\n");
        List<Map<String, String>> list = new ArrayList<>();
        Set<String> existing = new HashSet<>();
        for (String line : lines) {
            String word = line.trim();
            if (word.isEmpty()) continue;
            if (existing.contains(word)) continue;
            existing.add(word);
            Map<String, String> item = new HashMap<>();
            item.put("word", word);
            item.put("category", "other");
            list.add(item);
        }
        if (!list.isEmpty()) {
            sensitiveWordMapper.batchInsert(list);
            sensitiveWordChecker.clearCache();
            log.info("批量导入 {} 个敏感词", list.size());
        }
    }
}