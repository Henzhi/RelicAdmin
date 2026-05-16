package com.relic.service.impl;

import com.relic.mapper.AuditRecordMapper;
import com.relic.mapper.AuditStrategyMapper;
import com.relic.mapper.NotificationMapper;
import com.relic.service.ContentModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentModerationServiceImpl implements ContentModerationService {

    private final AuditRecordMapper auditRecordMapper;
    private final AuditStrategyMapper auditStrategyMapper;
    private final SensitiveWordChecker sensitiveWordChecker;
    private final NotificationMapper notificationMapper;

    @Override
    public Map<String, Object> submitContent(String contentId, String contentType, String content, Integer submitterId) {
        return doSubmit(contentId, contentType, content, submitterId, null);
    }

    @Override
    public Map<String, Object> submitContentWithImage(String contentId, String contentType, String content,
                                                      Integer submitterId, String imageUrl) {
        return doSubmit(contentId, contentType, content, submitterId, imageUrl);
    }

    private Map<String, Object> doSubmit(String contentId, String contentType, String content,
                                          Integer submitterId, String imageUrl) {
        String autoResult = "approved";
        List<String> hitWords = new ArrayList<>();
        boolean imageBlocked = false;

        Map<String, Object> strategy = auditStrategyMapper.selectByContentType(contentType);
        if (strategy == null || strategy.isEmpty()) {
            strategy = getDefaultStrategy();
        }

        Integer enableSensitive = strategy.get("enableSensitiveCheck") != null
                ? (Integer) strategy.get("enableSensitiveCheck") : 1;
        Integer enableImage = strategy.get("enableImageCheck") != null
                ? (Integer) strategy.get("enableImageCheck") : 0;

        if (enableSensitive == 1 && content != null && !content.isBlank()) {
            hitWords = sensitiveWordChecker.checkText(content);
            if (!hitWords.isEmpty()) {
                log.info("内容命中敏感词: contentId={}, hits={}", contentId, hitWords);
                autoResult = "rejected";
            }
        }

        if (enableImage == 1 && imageUrl != null && !imageUrl.isBlank()) {
            imageBlocked = simulateImageCheck(imageUrl);
            if (imageBlocked) {
                log.info("图片审核不通过: contentId={}, imageUrl={}", contentId, imageUrl);
                autoResult = "rejected";
            }
        }

        if ("rejected".equals(autoResult)) {
            auditRecordMapper.updateByContentId(contentId, autoResult, null);
            notificationMapper.insert(submitterId, "audit_result", "内容被拦截",
                    "您提交的内容因包含违规信息被自动拦截，请修改后重新提交",
                    "{\"contentId\":\"" + contentId + "\"}");
        } else {
            auditRecordMapper.updateByContentId(contentId, "pending", null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("autoAuditResult", autoResult);
        result.put("hitWords", hitWords);
        result.put("imageBlocked", imageBlocked);
        return result;
    }

    private boolean simulateImageCheck(String imageUrl) {
        return imageUrl.contains("violation") || imageUrl.contains("blocked");
    }

    private Map<String, Object> getDefaultStrategy() {
        Map<String, Object> s = new HashMap<>();
        s.put("enableSensitiveCheck", 1);
        s.put("enableImageCheck", 0);
        s.put("riskThreshold", 2);
        return s;
    }
}