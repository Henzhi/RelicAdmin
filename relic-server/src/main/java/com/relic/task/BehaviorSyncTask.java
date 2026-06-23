package com.relic.task;

import com.relic.entity.UserBehavior;
import com.relic.mapper.UserBehaviorMapper;
import com.relic.mapper.UserCommentMapper;
import com.relic.mapper.UserPostMapper;
import com.relic.mapper.UserUploadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 定时扫描 user_posts、user_comments、user_uploads 表，
 * 将新增记录同步到 user_behaviors 行为日志表。
 * 由于其他子系统的后端不在本项目中，无法通过服务调用记录行为，
 * 因此采用查表同步的方式。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BehaviorSyncTask {

    private final UserPostMapper userPostMapper;
    private final UserCommentMapper userCommentMapper;
    private final UserUploadMapper userUploadMapper;
    private final UserBehaviorMapper userBehaviorMapper;

    private static final int BATCH_SIZE = 200;

    /**
     * 每5分钟执行一次，扫描三张表中尚未同步到 user_behaviors 的记录
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 30 * 1000)
    public void syncBehaviors() {
        int postCount = syncPosts();
        int commentCount = syncComments();
        int uploadCount = syncUploads();

        int total = postCount + commentCount + uploadCount;
        if (total > 0) {
            log.info("行为同步完成: 发布={}, 评论={}, 上传={}, 总计={}", postCount, commentCount, uploadCount, total);
        }
    }

    private int syncPosts() {
        try {
            List<Map<String, Object>> unsynced = userPostMapper.selectUnsynced(BATCH_SIZE);
            for (Map<String, Object> row : unsynced) {
                UserBehavior behavior = new UserBehavior();
                behavior.setUserId(toInt(row.get("user_id")));
                behavior.setBehaviorType("publish");
                behavior.setTargetType("post");
                behavior.setTargetId(String.valueOf(row.get("id")));
                behavior.setTargetDesc(truncate(String.valueOf(row.getOrDefault("content", "")), 200));
                behavior.setCreatedAt(toLocalDateTime(row.get("created_at")));
                userBehaviorMapper.insert(behavior);
            }
            return unsynced.size();
        } catch (Exception e) {
            log.error("同步发布行为失败", e);
            return 0;
        }
    }

    private int syncComments() {
        try {
            List<Map<String, Object>> unsynced = userCommentMapper.selectUnsynced(BATCH_SIZE);
            for (Map<String, Object> row : unsynced) {
                UserBehavior behavior = new UserBehavior();
                behavior.setUserId(toInt(row.get("user_id")));
                behavior.setBehaviorType("comment");
                behavior.setTargetType("comment");
                behavior.setTargetId(String.valueOf(row.get("id")));
                behavior.setTargetDesc(truncate(String.valueOf(row.getOrDefault("content", "")), 200));
                behavior.setCreatedAt(toLocalDateTime(row.get("created_at")));
                userBehaviorMapper.insert(behavior);
            }
            return unsynced.size();
        } catch (Exception e) {
            log.error("同步评论行为失败", e);
            return 0;
        }
    }

    private int syncUploads() {
        try {
            List<Map<String, Object>> unsynced = userUploadMapper.selectUnsynced(BATCH_SIZE);
            for (Map<String, Object> row : unsynced) {
                UserBehavior behavior = new UserBehavior();
                behavior.setUserId(toInt(row.get("user_id")));
                behavior.setBehaviorType("upload");
                behavior.setTargetType("upload");
                behavior.setTargetId(String.valueOf(row.get("id")));
                behavior.setTargetDesc(truncate(String.valueOf(row.getOrDefault("caption", "")), 200));
                behavior.setCreatedAt(toLocalDateTime(row.get("created_at")));
                userBehaviorMapper.insert(behavior);
            }
            return unsynced.size();
        } catch (Exception e) {
            log.error("同步上传行为失败", e);
            return 0;
        }
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(String.valueOf(val));
    }

    private LocalDateTime toLocalDateTime(Object val) {
        if (val == null) return LocalDateTime.now();
        if (val instanceof LocalDateTime) return (LocalDateTime) val;
        if (val instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        return LocalDateTime.now();
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return null;
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }
}
