package com.relic.service.impl;

import com.relic.mapper.AuditRecordMapper;
import com.relic.mapper.AuditStrategyMapper;
import com.relic.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 内容审核服务单元测试：身份使用服务端传入的 submitterId，审核状态更新必须携带 contentType+submitterId 属主限定
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContentModerationServiceImplTest {

    @Mock
    private AuditRecordMapper auditRecordMapper;

    @Mock
    private AuditStrategyMapper auditStrategyMapper;

    @Mock
    private SensitiveWordChecker sensitiveWordChecker;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private ContentModerationServiceImpl service;

    private Map<String, Object> strategy(int enableSensitive, int enableImage) {
        Map<String, Object> s = new HashMap<>();
        s.put("enableSensitiveCheck", enableSensitive);
        s.put("enableImageCheck", enableImage);
        return s;
    }

    @Test
    void submitContent_hitSensitiveWord_shouldRejectAndNotify() {
        when(auditStrategyMapper.selectByContentType("comment")).thenReturn(strategy(1, 0));
        when(sensitiveWordChecker.checkText("违禁内容")).thenReturn(List.of("违禁"));

        Map<String, Object> result = service.submitContent("post_1", "comment", "违禁内容", 5);

        assertEquals("rejected", result.get("autoAuditResult"));
        // 更新必须限定 contentId + contentType + submitterId，防止影响他人记录
        verify(auditRecordMapper).updateByContentId("post_1", "comment", 5, "rejected", null);
        verify(notificationMapper).insert(eq(5), eq("audit_result"), anyString(), anyString(), anyString());
    }

    @Test
    void submitContent_cleanContent_shouldMarkPendingWithoutNotification() {
        when(auditStrategyMapper.selectByContentType("comment")).thenReturn(strategy(1, 0));
        when(sensitiveWordChecker.checkText("正常内容")).thenReturn(List.of());

        Map<String, Object> result = service.submitContent("post_2", "comment", "正常内容", 5);

        assertEquals("approved", result.get("autoAuditResult"));
        verify(auditRecordMapper).updateByContentId("post_2", "comment", 5, "pending", null);
        verify(notificationMapper, never()).insert(anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void submitContent_strategyMissing_shouldFallbackToDefaultSensitiveCheck() {
        // 策略缺失时默认启用敏感词检查：命中即拒绝
        when(auditStrategyMapper.selectByContentType("post")).thenReturn(null);
        when(sensitiveWordChecker.checkText("任意")).thenReturn(List.of("违禁"));

        Map<String, Object> result = service.submitContent("comment_3", "post", "任意", 6);

        assertEquals("rejected", result.get("autoAuditResult"));
        verify(auditRecordMapper).updateByContentId("comment_3", "post", 6, "rejected", null);
    }

    @Test
    void submitContent_imageBlocked_shouldReject() {
        when(auditStrategyMapper.selectByContentType("upload")).thenReturn(strategy(1, 1));

        Map<String, Object> result = service.submitContentWithImage(
                "upload_9", "upload", "看图", 7, "http://x/violation.jpg");

        assertEquals("rejected", result.get("autoAuditResult"));
        assertEquals(Boolean.TRUE, result.get("imageBlocked"));
        verify(auditRecordMapper).updateByContentId("upload_9", "upload", 7, "rejected", null);
    }

    @Test
    void submitContent_blankContentId_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> service.submitContent(" ", "comment", "内容", 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.submitContent("post_1", null, "内容", 5));
        verify(auditRecordMapper, never()).updateByContentId(
                anyString(), anyString(), anyInt(), anyString(), anyString());
    }
}
