package com.relic.controller.admin;

import com.relic.dto.PenaltyCreateDTO;
import com.relic.dto.PenaltyRevokeDTO;
import com.relic.result.Result;
import com.relic.service.PenaltyService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "管理端-违规处罚管理", description = "违规类型查看、处罚用户、处罚记录管理")
public class PenaltyAdminController {

    private final PenaltyService penaltyService;

    @GetMapping("/admin/violation-type/list")
    public Result<PageResultVO<Map<String, Object>>> listViolationTypes() {
        return Result.success(penaltyService.listViolationTypes());
    }

    @PostMapping("/admin/penalty")
    public Result<Void> createPenalty(@RequestBody PenaltyCreateDTO dto) {
        penaltyService.createPenalty(dto);
        return Result.success();
    }

    @GetMapping("/admin/penalty/page")
    public Result<PageResultVO<Map<String, Object>>> penaltyPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String penaltyType,
            @RequestParam(required = false) Integer status) {
        return Result.success(penaltyService.listPenalties(userId, penaltyType, status, page, pageSize));
    }

    @PutMapping("/admin/penalty/{id}/revoke")
    public Result<Void> revokePenalty(@PathVariable Long id, @RequestBody PenaltyRevokeDTO dto) {
        penaltyService.revokePenalty(id, dto);
        return Result.success();
    }
}