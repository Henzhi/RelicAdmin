package com.relic.controller.admin;

import com.relic.dto.DynastyCreateDTO;
import com.relic.dto.DynastyUpdateDTO;
import com.relic.result.Result;
import com.relic.service.DynastyService;
import com.relic.vo.DynastyVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dynasty")
@RequiredArgsConstructor
@Tag(name = "管理端朝代管理", description = "朝代CRUD")
public class AdminDynastyController {

    private final DynastyService dynastyService;

    @GetMapping("/page")
    public Result<PageResultVO<DynastyVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String nameZh) {
        return Result.success(dynastyService.page(nameZh, page, pageSize));
    }

    @GetMapping("/list")
    public Result<List<DynastyVO>> list() {
        return Result.success(dynastyService.listAll());
    }

    @PostMapping
    public Result<Void> create(@RequestBody DynastyCreateDTO dto) {
        dynastyService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody DynastyUpdateDTO dto) {
        dynastyService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        dynastyService.delete(id);
        return Result.success();
    }
}