package com.relic.service.impl;

import com.relic.dto.CrawlTaskCreateDTO;
import com.relic.dto.CrawlTaskUpdateDTO;
import com.relic.mapper.CrawlTaskLogMapper;
import com.relic.mapper.CrawlTaskMapper;
import com.relic.service.CrawlTaskService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlTaskServiceImpl implements CrawlTaskService {

    private final CrawlTaskMapper crawlTaskMapper;
    private final CrawlTaskLogMapper crawlTaskLogMapper;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResultVO<Map<String, Object>> listPage(String status, Integer priority, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = crawlTaskMapper.selectByPage(status, priority, offset, pageSize);
        long total = crawlTaskMapper.countByPage(status, priority);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> task = crawlTaskMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("爬取任务不存在");
        }
        List<Map<String, Object>> recentLogs = crawlTaskLogMapper.selectRecentByTaskId(id, 5);
        task.put("recentLogs", recentLogs);
        return task;
    }

    @Override
    @Transactional
    public void create(CrawlTaskCreateDTO dto, Integer createdBy) {
        if (dto.getTaskName() == null || dto.getTaskName().isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }
        if (dto.getTaskCode() == null || dto.getTaskCode().isBlank()) {
            throw new IllegalArgumentException("任务编码不能为空");
        }
        if (dto.getSourceUrl() == null || dto.getSourceUrl().isBlank()) {
            throw new IllegalArgumentException("数据源URL不能为空");
        }
        if (crawlTaskMapper.checkTaskCodeExists(dto.getTaskCode()) > 0) {
            throw new IllegalArgumentException("任务编码已存在");
        }
        if (dto.getPriority() == null) dto.setPriority(1);
        if (dto.getMaxRetry() == null) dto.setMaxRetry(3);
        if (dto.getRetryDelay() == null) dto.setRetryDelay(60);
        if (dto.getTimeoutSeconds() == null) dto.setTimeoutSeconds(300);
        if (dto.getEnabled() == null) dto.setEnabled(1);

        crawlTaskMapper.insert(dto.getTaskName(), dto.getTaskCode(), dto.getSourceUrl(),
                dto.getSourceType(), dto.getCrawlRule(), dto.getPriority(),
                dto.getCronExpression(), dto.getMaxRetry(), dto.getRetryDelay(),
                dto.getTimeoutSeconds(), dto.getDescription(), dto.getEnabled(), createdBy);
        log.info("创建爬取任务: {}", dto.getTaskCode());
    }

    @Override
    @Transactional
    public void update(Integer id, CrawlTaskUpdateDTO dto) {
        Map<String, Object> existing = crawlTaskMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("爬取任务不存在");
        }
        if (dto.getTaskName() == null || dto.getTaskName().isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }
        crawlTaskMapper.update(id, dto.getTaskName(), dto.getSourceUrl(), dto.getSourceType(),
                dto.getCrawlRule(), dto.getPriority(), dto.getCronExpression(),
                dto.getMaxRetry(), dto.getRetryDelay(), dto.getTimeoutSeconds(),
                dto.getDescription(), dto.getEnabled());
        log.info("更新爬取任务 id: {}", id);
    }

    @Override
    @Transactional
    public void execute(Integer id, Integer executorId) {
        Map<String, Object> task = crawlTaskMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("爬取任务不存在");
        }
        String currentStatus = (String) task.get("status");
        if ("running".equals(currentStatus)) {
            throw new IllegalArgumentException("任务正在运行中，请稍后再试");
        }
        if ("paused".equals(currentStatus)) {
            throw new IllegalArgumentException("任务已暂停，请先恢复再执行");
        }

        String now = LocalDateTime.now().format(DF);
        String taskName = (String) task.get("taskName");

        crawlTaskMapper.updateStatus(id, "running");

        String startTime = LocalDateTime.now().format(DF);
        crawlTaskLogMapper.insert(id, taskName, startTime, "running", 0, null, 0, executorId);

        try {
            Thread.sleep((long) (2000 + Math.random() * 3000));
            int crawledCount = (int) (10 + Math.random() * 100);

            String endTimeStr = LocalDateTime.now().format(DF);
            Long logId = getLastInsertId(id);
            if (logId != null) {
                crawlTaskLogMapper.updateResult(logId.intValue(), endTimeStr, "success", crawledCount, null);
            }

            crawlTaskMapper.updateRunStats(id, "idle", now, null);
            log.info("任务执行成功: id={}, crawledCount={}", id, crawledCount);
        } catch (Exception e) {
            log.error("任务执行失败: id={}, error={}", id, e.getMessage());
            String endTimeStr = LocalDateTime.now().format(DF);
            Long logId = getLastInsertId(id);
            if (logId != null) {
                crawlTaskLogMapper.updateResult(logId.intValue(), endTimeStr, "failed", 0, e.getMessage());
            }
            crawlTaskMapper.updateRunStats(id, "failed", now, null);
            throw new RuntimeException("任务执行失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void pause(Integer id) {
        Map<String, Object> task = crawlTaskMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("爬取任务不存在");
        }
        if (!"running".equals(task.get("status"))) {
            throw new IllegalArgumentException("只能暂停运行中的任务");
        }
        crawlTaskMapper.updateStatus(id, "paused");
        log.info("暂停爬取任务 id: {}", id);
    }

    @Override
    @Transactional
    public void resume(Integer id) {
        Map<String, Object> task = crawlTaskMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("爬取任务不存在");
        }
        if (!"paused".equals(task.get("status"))) {
            throw new IllegalArgumentException("只能恢复已暂停的任务");
        }
        crawlTaskMapper.updateStatus(id, "idle");
        log.info("恢复爬取任务 id: {}", id);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        crawlTaskMapper.deleteById(id);
        log.info("删除爬取任务 id: {}", id);
    }

    @Override
    public PageResultVO<Map<String, Object>> listLogs(Integer taskId, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = crawlTaskLogMapper.selectByPage(taskId, status, offset, pageSize);
        long total = crawlTaskLogMapper.countByPage(taskId, status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    private Long getLastInsertId(Integer taskId) {
        List<Map<String, Object>> lastLog = crawlTaskLogMapper.selectRecentByTaskId(taskId, 1);
        if (lastLog != null && !lastLog.isEmpty()) {
            Object idObj = lastLog.get(0).get("id");
            if (idObj instanceof Long) return (Long) idObj;
            if (idObj instanceof Integer) return ((Integer) idObj).longValue();
        }
        return null;
    }
}