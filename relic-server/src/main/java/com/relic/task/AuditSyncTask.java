package com.relic.task;

import com.relic.mapper.AuditRecordMapper;
import com.relic.mapper.UserCommentMapper;
import com.relic.mapper.UserPostMapper;
import com.relic.mapper.UserUploadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定时扫描 user_posts、user_comments、user_uploads 三张源表，
 * 将尚未在 audit_records 中有记录的内容提取并写入审核记录表。
 * 由于其他子系统的后端不在本项目中，只能通过查表形式提取待审核数据。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditSyncTask {

    private final UserPostMapper userPostMapper;
    private final UserCommentMapper userCommentMapper;
    private final UserUploadMapper userUploadMapper;
    private final AuditRecordMapper auditRecordMapper;

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
                auditRecordMapper.insert(
                        "post_" + row.get("id"),
                        "post",
                        "user_posts",
                        truncate(String.valueOf(row.getOrDefault("content", "")), 500),
                        toInt(row.get("user_id"))
                );
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
                auditRecordMapper.insert(
                        "comment_" + row.get("id"),
                        "comment",
                        "user_comments",
                        truncate(String.valueOf(row.getOrDefault("content", "")), 500),
                        toInt(row.get("user_id"))
                );
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
                auditRecordMapper.insert(
                        "upload_" + row.get("id"),
                        "upload",
                        "user_uploads",
                        truncate(String.valueOf(row.getOrDefault("content", "")), 500),
                        toInt(row.get("user_id"))
                );
            }
            return unaudited.size();
        } catch (Exception e) {
            log.error("同步上传审核数据失败", e);
            return 0;
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
