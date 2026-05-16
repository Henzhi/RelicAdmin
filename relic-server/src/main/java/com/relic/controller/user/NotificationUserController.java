package com.relic.controller.user;

import com.relic.dto.NotificationReadDTO;
import com.relic.result.Result;
import com.relic.service.NotificationService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/notification")
@RequiredArgsConstructor
@Tag(name = "用户端-通知", description = "通知列表、标记已读、未读数量")
public class NotificationUserController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<PageResultVO<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer isRead) {
        return Result.success(notificationService.listUserNotifications(isRead, page, pageSize));
    }

    @PutMapping("/read")
    public Result<Void> markRead(@RequestBody NotificationReadDTO dto) {
        notificationService.markRead(dto);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Object>> unreadCount() {
        return Result.success(notificationService.getUnreadCount());
    }
}