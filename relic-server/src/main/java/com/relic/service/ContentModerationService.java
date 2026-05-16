package com.relic.service;

import java.util.Map;

public interface ContentModerationService {
    Map<String, Object> submitContent(String contentId, String contentType, String content, Integer submitterId);
    Map<String, Object> submitContentWithImage(String contentId, String contentType, String content,
                                               Integer submitterId, String imageUrl);
}