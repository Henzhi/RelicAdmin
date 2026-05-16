package com.relic.controller.user;

import com.relic.result.Result;
import com.relic.service.AnnouncementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/announcement")
@RequiredArgsConstructor
@Tag(name = "用户端-公告", description = "获取生效中的公告列表")
public class AnnouncementUserController {

    private final AnnouncementService announcementService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) String position) {
        return Result.success(announcementService.listActiveAnnouncements(position));
    }
}