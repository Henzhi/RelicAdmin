package com.relic.controller.admin;

import com.relic.dto.MuseumCreateDTO;
import com.relic.dto.MuseumUpdateDTO;
import com.relic.result.Result;
import com.relic.service.MuseumService;
import com.relic.vo.MuseumVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/museum")
@RequiredArgsConstructor
@Tag(name = "管理端博物馆管理", description = "博物馆CRUD")
public class AdminMuseumController {

    private final MuseumService museumService;

    @GetMapping("/page")
    public Result<PageResultVO<MuseumVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country) {
        return Result.success(museumService.page(name, country, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MuseumVO> getById(@PathVariable Integer id) {
        return Result.success(museumService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody MuseumCreateDTO dto) {
        museumService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody MuseumUpdateDTO dto) {
        museumService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        museumService.delete(id);
        return Result.success();
    }
}