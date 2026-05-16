package com.relic.controller.user;

import com.relic.dto.ContentSubmitDTO;
import com.relic.result.Result;
import com.relic.service.ContentModerationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/content")
@RequiredArgsConstructor
@Tag(name = "用户端-内容提交审核", description = "用户提交内容时触发自动审核")
public class ContentModerationController {

    private final ContentModerationService contentModerationService;

    @PostMapping("/submit")
    public Result<Map<String, Object>> submit(@RequestBody ContentSubmitDTO dto) {
        return Result.success(contentModerationService.submitContent(
                dto.getContentId(), dto.getContentType(), dto.getContent(), dto.getSubmitterId()));
    }

    @PostMapping("/submit-with-image")
    public Result<Map<String, Object>> submitWithImage(@RequestBody Map<String, Object> body) {
        String imageUrl = body.get("imageUrl") != null ? body.get("imageUrl").toString() : null;
        return Result.success(contentModerationService.submitContentWithImage(
                body.get("contentId").toString(),
                body.get("contentType").toString(),
                body.get("content") != null ? body.get("content").toString() : "",
                Integer.valueOf(body.get("submitterId").toString()),
                imageUrl));
    }
}