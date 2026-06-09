package com.relic.controller.admin;

import com.relic.properties.QaProperties;
import com.relic.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 知识问答子系统代理控制器
 * 将管理端的问答相关请求转发到知识问答子系统
 */
@RestController
@RequestMapping("/admin/qa")
@RequiredArgsConstructor
@Slf4j
public class QaProxyController {

    private final QaProperties qaProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 查询问答日志
     */
    @GetMapping("/logs")
    public Result<Object> getLogs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String intent,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        StringBuilder url = new StringBuilder(qaProperties.getBaseUrl() + "/api/admin/qa/logs?");
        appendParam(url, "page", page);
        appendParam(url, "pageSize", pageSize);
        appendParam(url, "status", status);
        appendParam(url, "intent", intent);
        appendParam(url, "keyword", keyword);
        appendParam(url, "startTime", startTime);
        appendParam(url, "endTime", endTime);
        log.info("代理请求-问答日志: {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url.toString(), Object.class);
            return Result.success(response.getBody());
        } catch (Exception e) {
            log.error("问答日志请求失败", e);
            return Result.error("问答子系统请求失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户反馈
     */
    @GetMapping("/feedback")
    public Result<Object> getFeedback(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String feedbackType,
            @RequestParam(required = false) String keyword) {
        StringBuilder url = new StringBuilder(qaProperties.getBaseUrl() + "/api/admin/qa/feedback?");
        appendParam(url, "page", page);
        appendParam(url, "pageSize", pageSize);
        appendParam(url, "feedbackType", feedbackType);
        appendParam(url, "keyword", keyword);
        log.info("代理请求-用户反馈: {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url.toString(), Object.class);
            return Result.success(response.getBody());
        } catch (Exception e) {
            log.error("用户反馈请求失败", e);
            return Result.error("问答子系统请求失败: " + e.getMessage());
        }
    }

    /**
     * 查询失败问题
     */
    @GetMapping("/failed-questions")
    public Result<Object> getFailedQuestions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String failureType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String intent,
            @RequestParam(required = false) String keyword) {
        StringBuilder url = new StringBuilder(qaProperties.getBaseUrl() + "/api/admin/qa/failed-questions?");
        appendParam(url, "page", page);
        appendParam(url, "pageSize", pageSize);
        appendParam(url, "failureType", failureType);
        appendParam(url, "status", status);
        appendParam(url, "intent", intent);
        appendParam(url, "keyword", keyword);
        log.info("代理请求-失败问题: {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url.toString(), Object.class);
            return Result.success(response.getBody());
        } catch (Exception e) {
            log.error("失败问题请求失败", e);
            return Result.error("问答子系统请求失败: " + e.getMessage());
        }
    }

    /**
     * 查询审核任务
     */
    @GetMapping("/review-tasks")
    public Result<Object> getReviewTasks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) String reviewResult) {
        StringBuilder url = new StringBuilder(qaProperties.getBaseUrl() + "/api/admin/qa/review-tasks?");
        appendParam(url, "page", page);
        appendParam(url, "pageSize", pageSize);
        appendParam(url, "taskStatus", taskStatus);
        appendParam(url, "reviewResult", reviewResult);
        log.info("代理请求-审核任务: {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url.toString(), Object.class);
            return Result.success(response.getBody());
        } catch (Exception e) {
            log.error("审核任务请求失败", e);
            return Result.error("问答子系统请求失败: " + e.getMessage());
        }
    }

    /**
     * 处理审核任务
     */
    @PostMapping("/review-tasks/{id}/review")
    public Result<Object> reviewTask(@PathVariable Long id, @RequestBody Map<String, Object> reviewBody) {
        String url = qaProperties.getBaseUrl() + "/api/admin/qa/review-tasks/" + id + "/review";
        log.info("代理请求-审核处理: taskId={}", id);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reviewBody, headers);
            ResponseEntity<Object> response = restTemplate.postForEntity(url, entity, Object.class);
            return Result.success(response.getBody());
        } catch (Exception e) {
            log.error("审核处理请求失败", e);
            return Result.error("问答子系统请求失败: " + e.getMessage());
        }
    }

    /**
     * 高频失败问题统计
     */
    @GetMapping("/statistics/failure-types")
    public Result<Object> getFailureTypeStats() {
        String url = qaProperties.getBaseUrl() + "/api/admin/qa/statistics/failure-types";
        log.info("代理请求-失败类型统计");
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return Result.success(response.getBody());
        } catch (Exception e) {
            log.error("失败类型统计请求失败", e);
            return Result.error("问答子系统请求失败: " + e.getMessage());
        }
    }

    /**
     * 高频不准确问题统计
     */
    @GetMapping("/statistics/inaccurate-types")
    public Result<Object> getInaccurateTypeStats() {
        String url = qaProperties.getBaseUrl() + "/api/admin/qa/statistics/inaccurate-types";
        log.info("代理请求-不准确类型统计");
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return Result.success(response.getBody());
        } catch (Exception e) {
            log.error("不准确类型统计请求失败", e);
            return Result.error("问答子系统请求失败: " + e.getMessage());
        }
    }

    private void appendParam(StringBuilder url, String name, Object value) {
        if (value != null) {
            url.append(name).append("=").append(value).append("&");
        }
    }
}
