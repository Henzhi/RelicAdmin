package com.relic.controller.admin;

import com.relic.dto.ArtistCreateDTO;
import com.relic.dto.ArtistUpdateDTO;
import com.relic.result.Result;
import com.relic.service.ArtistService;
import com.relic.vo.ArtistVO;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/artist")
@RequiredArgsConstructor
@Tag(name = "管理端艺术家管理", description = "艺术家CRUD")
public class AdminArtistController {

    private final ArtistService artistService;

    @GetMapping("/page")
    public Result<PageResultVO<ArtistVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String nameZh,
            @RequestParam(required = false) String nameEn,
            @RequestParam(required = false) Integer dynastyId) {
        return Result.success(artistService.page(nameZh, nameEn, dynastyId, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ArtistVO> getById(@PathVariable Integer id) {
        return Result.success(artistService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody ArtistCreateDTO dto) {
        artistService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody ArtistUpdateDTO dto) {
        artistService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        artistService.delete(id);
        return Result.success();
    }
}