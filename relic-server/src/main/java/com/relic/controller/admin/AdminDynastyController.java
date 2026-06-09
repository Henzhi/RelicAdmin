package com.relic.controller.admin;

import com.relic.entity.Dynasty;
import com.relic.result.Result;
import com.relic.service.DynastyService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dynasty")
@RequiredArgsConstructor
public class AdminDynastyController {

    private final DynastyService dynastyService;

    @GetMapping("/page")
    public Result<PageResultVO<Dynasty>> page(
            @RequestParam(required = false) String nameZh,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(dynastyService.page(nameZh, page, pageSize));
    }

    @GetMapping("/list")
    public Result<List<Dynasty>> list() {
        return Result.success(dynastyService.page(null, 1, 1000).getRecords());
    }

    @GetMapping("/{id}")
    public Result<Dynasty> getById(@PathVariable Integer id) {
        return Result.success(dynastyService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Dynasty dynasty) {
        dynastyService.create(dynasty);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Dynasty dynasty) {
        dynasty.setId(id);
        dynastyService.update(dynasty);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        dynastyService.delete(id);
        return Result.success();
    }
}
