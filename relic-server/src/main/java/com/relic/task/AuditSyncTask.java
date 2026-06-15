package com.relic.task;

import com.relic.mapper.AuditRecordMapper;
import com.relic.mapper.UserCommentMapper;
import com.relic.mapper.UserPostMapper;
import com.relic.mapper.UserUploadMapper;
import com.relic.service.AuditStrategyService;
import com.relic.service.impl.SensitiveWordChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定时扫描 user_posts、user_comments、user_uploads 三张源表，
 * 将尚未在 audit_records 中有记录的内容提取并写入审核记录表。
 * 同步时执行自动审核：根据审核策略和敏感词检测决定自动审核结果。
 * - auto_pass 模式：自动通过，流程终结
 * - auto_review 模式：敏感词检测，通过则自动通过，否则转人工
 * - auto_reject 模式：自动拒绝，流程终结
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditSyncTask {

    private final UserPostMapper userPostMapper;
    private final UserCommentMapper userCommentMapper;
    private final UserUploadMapper userUploadMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final AuditStrategyService auditStrategyService;
    private final SensitiveWordChecker sensitiveWordChecker;

    private static final int BATCH_SIZE = 200;

    /**
     * 每3分钟执行一次，扫描三张源表中尚未同步到 audit_records 的记录
     */
    @Scheduled(fixedRate = 3 * 60 * 1000, initialDelay = 20 * 1000)
    public void syncAuditRecords() {
        int postCount = syncPosts();
        int commentCount = syncComments();
        int uploadCount = syncUploads();

        int total = postCount + commentCount + uploadCount;
        if (total > 0) {
            log.info("审核数据同步完成: 动态={}, 评论={}, 上传={}, 总计={}", postCount, commentCount, uploadCount, total);
        }
    }

    private int syncPosts() {
        try {
            List<Map<String, Object>> unaudited = userPostMapper.selectUnaudited(BATCH_SIZE);
            for (Map<String, Object> row : unaudited) {
                String content = truncate(String.valueOf(row.getOrDefault("content", "")), 500);
                Integer submitterId = toInt(row.get("user_id"));
                String autoResult = executeAutoAudit("post", content);
                String manualResult = "approved".equals(autoResult) ? "approved" : "pending";
                auditRecordMapper.insert(
                        "post_" + row.get("id"),
                        "post",
                        "user_posts",
                        content,
                        submitterId,
                        autoResult,
                        manualResult
                );
                // 自动审核通过时同步更新源表状态
                if ("approved".equals(autoResult)) {
                    syncSourceApproved("user_posts", toInt(row.get("id")));
                }
            }
            return unaudited.size();
        } catch (Exception e) {
            log.error("同步动态审核数据失败", e);
            return 0;
        }
    }

    private int syncComments() {
        try {
            List<Map<String, Object>> unaudited = userCommentMapper.selectUnaudited(BATCH_SIZE);
            for (Map<String, Object> row : unaudited) {
                String content = truncate(String.valueOf(row.getOrDefault("content", "")), 500);
                Integer submitterId = toInt(row.get("user_id"));
                String autoResult = executeAutoAudit("comment", content);
                String manualResult = "approved".equals(autoResult) ? "approved" : "pending";
                auditRecordMapper.insert(
                        "comment_" + row.get("id"),
                        "comment",
                        "user_comments",
                        content,
                        submitterId,
                        autoResult,
                        manualResult
                );
                if ("approved".equals(autoResult)) {
                    syncSourceApproved("user_comments", toInt(row.get("id")));
                }
            }
            return unaudited.size();
        } catch (Exception e) {
            log.error("同步评论审核数据失败", e);
            return 0;
        }
    }

    private int syncUploads() {
        try {
            List<Map<String, Object>> unaudited = userUploadMapper.selectUnaudited(BATCH_SIZE);
            for (Map<String, Object> row : unaudited) {
                String content = truncate(String.valueOf(row.getOrDefault("content", "")), 500);
                Integer submitterId = toInt(row.get("user_id"));
                String autoResult = executeAutoAudit("upload", content);
                String manualResult = "approved".equals(autoResult) ? "approved" : "pending";
                auditRecordMapper.insert(
                        "upload_" + row.get("id"),
                        "upload",
                        "user_uploads",
                        content,
                        submitterId,
                        autoResult,
                        manualResult
                );
                if ("approved".equals(autoResult)) {
                    syncSourceApproved("user_uploads", toInt(row.get("id")));
                }
            }
            return unaudited.size();
        } catch (Exception e) {
            log.error("同步上传审核数据失败", e);
            return 0;
        }
    }

    /**
     * 执行自动审核逻辑：
     * 1. auto_pass → 直接通过
     * 2. auto_reject → 直接拒绝
     * 3. auto_review → 计算风险评分，与风险阈值比较：
     *    - 风险评分 < 阈值 → 自动通过
     *    - 风险评分 >= 阈值 → 自动拒绝，转人工复核
     * 
     * 风险评分规则：
     * - 基础分 0
     * - 命中敏感词：每个 +2
     * - 内容过短（<5字）：+1
     * - 包含URL链接：+1
     * - 内容含特殊字符过多：+1
     */
    private String executeAutoAudit(String contentType, String content) {
        Map<String, Object> strategy = auditStrategyService.getStrategy(contentType);
        String autoMode = String.valueOf(strategy.getOrDefault("autoMode", "auto_review"));

        switch (autoMode) {
            case "auto_pass":
                return "approved";
            case "auto_reject":
                return "rejected";
            case "auto_review":
            default:
                return executeAutoReview(strategy, content);
        }
    }

    /**
     * auto_review 模式下的自动审核：计算风险评分并与阈值比较
     */
    private String executeAutoReview(Map<String, Object> strategy, String content) {
        int riskThreshold = getRiskThreshold(strategy);
        int riskScore = calculateRiskScore(strategy, content);
        log.debug("自动审核风险评分: score={}, threshold={}", riskScore, riskThreshold);

        if (riskScore < riskThreshold) {
            return "approved";
        } else {
            log.info("自动审核风险评分 {} >= 阈值 {}，转人工复核", riskScore, riskThreshold);
            return "rejected";
        }
    }

    /**
     * 计算内容的风险评分
     */
    private int calculateRiskScore(Map<String, Object> strategy, String content) {
        int score = 0;

        // 1. 敏感词检测：每个命中 +2
        boolean enableSensitiveCheck = "1".equals(String.valueOf(strategy.getOrDefault("enableSensitiveCheck", 1)));
        if (enableSensitiveCheck && content != null && !content.isBlank()) {
            List<String> hits = sensitiveWordChecker.checkText(content);
            if (!hits.isEmpty()) {
                score += hits.size() * 2;
                log.info("敏感词命中 {} 个: {}", hits.size(), hits);
            }
        }

        // 2. 内容过短（<5字）：+1
        if (content == null || content.isBlank() || content.trim().length() < 5) {
            score += 1;
        }

        // 3. 包含URL链接：+1
        if (content != null && content.matches(".*https?://.*")) {
            score += 1;
        }

        // 4. 特殊字符占比过高（>30%）：+1
        if (content != null && content.length() > 10) {
            long specialCount = content.chars().filter(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c)).count();
            if ((double) specialCount / content.length() > 0.3) {
                score += 1;
            }
        }

        return score;
    }

    /**
     * 获取风险阈值，默认为 2
     */
    private int getRiskThreshold(Map<String, Object> strategy) {
        Object threshold = strategy.get("riskThreshold");
        if (threshold == null) return 2;
        if (threshold instanceof Number) return ((Number) threshold).intValue();
        try {
            return Integer.parseInt(String.valueOf(threshold));
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    /**
     * 自动审核通过时，同步更新源表审核状态为 approved
     */
    private void syncSourceApproved(String sourceType, Integer sourceId) {
        try {
            switch (sourceType) {
                case "user_posts" -> userPostMapper.updateAuditStatus(sourceId, "approved", null, null);
                case "user_comments" -> userCommentMapper.updateAuditStatus(sourceId, "approved", null, null);
                case "user_uploads" -> userUploadMapper.updateAuditStatus(sourceId, "approved", null, null);
            }
        } catch (Exception e) {
            log.error("自动审核通过后同步源表状态失败, sourceType={}, sourceId={}", sourceType, sourceId, e);
        }
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(String.valueOf(val));
    }

    private String truncate(String s, int maxLen) {
        if (s == null || "null".equals(s)) return null;
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }
}
